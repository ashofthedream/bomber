package ashes.of.bomber.runner;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.State;
import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.stopwatch.Stopwatch;
import ashes.of.bomber.sink.AsyncSink;
import ashes.of.bomber.sink.MultiSink;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.squadron.Barrier;
import ashes.of.bomber.stopwatch.Clock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

import static java.util.concurrent.TimeUnit.SECONDS;


public class Runner<T> {
    private static final Logger log = LogManager.getLogger();

    private final WorkerPool pool;
    private final State state;
    private final Settings settings;
    private final Environment env;
    private final LifeCycle<T> lifeCycle;
    private final Sink sink;
    private final Barrier barrier;

    private final List<Worker> workers = new ArrayList<>();

    public Runner(WorkerPool pool, State state, Environment env, LifeCycle<T> lifeCycle) {
        this.pool = pool;
        this.state = state;
        this.settings = state.getSettings();
        this.env = env;
        this.lifeCycle = lifeCycle;
        this.sink = new AsyncSink(new MultiSink(env.getSinks()));
        this.barrier = env.getBarrier()
                .workers(settings.getThreadsCount())
                .build();
    }

    public State getState() {
        return state;
    }

    public void run() {
        run(state.getSettings());
    }

    /**
     * Runs the test case
     */
    public void run(Settings settings) {
        ThreadContext.put("stage", state.getStage().name());
        ThreadContext.put("testSuite", state.getTestCase());
        log.info("Start with settings: {}", settings);

        barrier.enterSuite(state.getStage(), state.getTestSuite(), settings);
        state.startSuiteIfNotStarted();
        sink.beforeTestSuite(state.getStage(), state.getTestSuite(), state.getTestSuiteStartTime(), settings);

        CountDownLatch begin = new CountDownLatch(settings.getThreadsCount());
        CountDownLatch end = new CountDownLatch(settings.getThreadsCount());

        for (int i = 0; i < settings.getThreadsCount(); i++) {
            Worker worker = pool.acquire();
            workers.add(worker);
            worker.run(() -> runTestSuite(settings, begin, end, barrier));
        }

        try {
            log.debug("Await end of stage");
            end.await();
        } catch (InterruptedException e) {
            log.error("We've been interrupted", e);
        }

        pool.release(workers);
        log.info("Ended, elapsed {}ms", state.getCaseElapsedTime());

        barrier.leaveSuite(state.getStage(), state.getTestSuite(), settings);
        sink.afterTestSuite(state.getStage(), state.getTestSuite());

        ThreadContext.clearAll();
    }

    private void runTestSuite(Settings settings, CountDownLatch startLatch, CountDownLatch endLatch, Barrier barrier) {
        ThreadContext.put("stage", state.getStage().name());
        ThreadContext.put("testSuite", state.getTestSuite());
        log.debug("run test suite");
        try {
            T instance = lifeCycle.testSuite();
            Limiter limiter = env.getLimiter().get();

            startLatch.countDown();

            log.trace("await start");
            if (!startLatch.await(60, SECONDS))
                log.warn("after 60s of waiting, worker started without all other workers (this isn't acceptable situation)");

            lifeCycle.beforeAll(state, instance);
            lifeCycle.testCases()
                    .forEach((name, testCase) -> runTestCase(settings, instance, testCase, limiter, barrier));
            lifeCycle.afterAll(state, instance);

        } catch (Throwable th) {
            log.warn("run test suite failed", th);
        }

        log.debug("finish test suite");
        endLatch.countDown();
        ThreadContext.clearAll();
    }


    private void runTestCase(Settings settings, T instance, TestCase<T> testCase, Limiter limiter, Barrier barrier) {
        ThreadContext.put("testCase", testCase.getName());
        String threadName = Thread.currentThread().getName();
        AtomicLong invocations = new AtomicLong();

        log.trace("run test case, enter");
        barrier.enterCase(state.getStage(), state.getTestSuite(), testCase.getName());
        state.startCaseIfNotStarted(testCase.getName());
        sink.beforeTestCase(state.getStage(), state.getTestSuite(), testCase.getName(), state.getTestCaseStartTime(), state.getSettings());
        log.debug("run test case");

        BooleanSupplier checker = state.createChecker();
        while (checker.getAsBoolean()) {
            if (!limiter.waitForPermit())
                throw new RuntimeException("Limiter await failed");

            long inv = invocations.getAndIncrement();
            Context context = new Context(state.getStage(), state.getTestSuite(), testCase.getName(), threadName, inv, Instant.now());

            lifeCycle.beforeEach(context, instance);

            Clock clock = new Clock(context.getTestSuite() + "." + testCase.getName(), record -> {
                sink.timeRecorded(context, record);

                if (!record.isSuccess())
                    state.incError();
            });

            Stopwatch stopwatch = clock.stopwatch("");
            try {
                // test
                testCase.getMethod().run(instance, clock);

                if (!testCase.isAsync())
                    stopwatch.success();

                sink.afterEach(context, stopwatch.getElapsed(), null);
            } catch (Throwable th) {
                if (!testCase.isAsync())
                    stopwatch.fail(th);

                log.trace("{} | runTestCase failed, inv: {}", state, inv, th);
                sink.afterEach(context, stopwatch.getElapsed(), th);
            }

            lifeCycle.afterEach(context, instance);
        }

        log.trace("finish test case, leave");
        barrier.leaveCase(state.getStage(), state.getTestSuite(), testCase.getName());
        sink.afterTestCase(state.getStage(), state.getTestSuite(), testCase.getName());
        state.finishCase();
        log.debug("finish test case");
    }
}
