package ashes.of.bomber.runner;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.State;
import ashes.of.bomber.core.limiter.Limiter;
import ashes.of.bomber.core.stopwatch.Record;
import ashes.of.bomber.core.stopwatch.Stopwatch;
import ashes.of.bomber.sink.AsyncSink;
import ashes.of.bomber.sink.MultiSink;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.squadron.Barrier;
import ashes.of.bomber.core.stopwatch.Clock;
import ashes.of.bomber.methods.TestCaseMethod;
import ashes.of.bomber.watchdog.Watchdog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

import static java.util.concurrent.TimeUnit.SECONDS;


public class Runner<T> {
    private static final Logger log = LogManager.getLogger();

    private final State state;
    private final Environment env;
    private final LifeCycle<T> lifeCycle;
    private final Sink sink;

    private final List<Worker> workers = new ArrayList<>();

    public Runner(State state, Environment env, LifeCycle<T> lifeCycle) {
        this.state = state;
        this.env = env;
        this.lifeCycle = lifeCycle;
        this.sink = new AsyncSink(new MultiSink(env.getSinks()));
    }

    public State getState() {
        return state;
    }

    /**
     * Runs the test case
     */
    public void run() {
        Settings settings = state.getSettings();
        log.info("Start stage: {}, testSuite: {}, settings: {}",
                state.getStage(), state.getTestSuite(), settings);

        if (settings.isDisabled()) {
            log.info("End stage: {}, testSuite: {} is disabled, exit", state.getStage(), state.getTestSuite());
            return;
        }

        state.startIfNotStarted();

        sink.beforeTestSuite(state.getStage(), state.getTestSuite(), state.getStartTime(), settings);

        CountDownLatch begin = new CountDownLatch(settings.getThreadsCount());
        CountDownLatch end = new CountDownLatch(settings.getThreadsCount());


        Barrier barrier = env.getBarrier()
                .workers(settings.getThreadsCount())
                .build();

        barrier.init(state.getTestSuite(), settings);

        barrier.stageStart(state.getStage());


        startWatchdogThread(begin, end);
        startWorkerThreads(begin, end, barrier);

        try {
            log.info("Await for end of stage: {}, testSuite: {}, elapsed {}ms",
                    state.getStage(), state.getTestSuite(), state.getElapsedTime());
            end.await();
        } catch (InterruptedException e) {
            log.error("We'he been interrupted", e);
        }

        log.info("End stage: {}, testSuite: {} elapsed {}ms",
                state.getStage(), state.getTestSuite(), state.getElapsedTime());

        sink.afterTestSuite(state.getStage(), state.getTestSuite(), state.getStartTime(), settings);
        barrier.stageLeave(state.getStage());
    }


    private void startWatchdogThread(CountDownLatch startLatch, CountDownLatch endLatch) {
        new Watchdog(this, env.getWatchers(), startLatch, endLatch).startInNewThread();
    }

    private void startWorkerThreads(CountDownLatch startLatch, CountDownLatch endLatch, Barrier barrier) {
        for (int i = 0; i < state.getSettings().getThreadsCount(); i++)
            startWorkerThread(() -> runTestSuite(startLatch, endLatch, barrier), i);
    }

    private void startWorkerThread(Runnable runnable, int index) {
        Thread thread = new Thread(runnable);
        thread.setUncaughtExceptionHandler((t, e) -> log.error("Uncaught exception in thread: {}", t.getName(), e));
        thread.setName(String.format("%s-%s-worker-%03d", state.getStage(), state.getTestSuite(), index));
        thread.start();

        workers.add(new Worker(thread));
    }


    private void runTestSuite(CountDownLatch startLatch, CountDownLatch endLatch, Barrier barrier) {
        log.debug("runTestCase stage: {}, testSuite: {}", state.getStage(), state.getTestSuite());
        try {
            T testSuite = lifeCycle.testSuite();
            Limiter limiter = env.getLimiter().get();

            startLatch.countDown();

            // if we can't start in 60 seconds – something works bad
            startLatch.await(60, SECONDS);

            lifeCycle.beforeAll(state, testSuite);
            lifeCycle.testCases()
                    .forEach((name, testCase) -> runTestCase(name, testSuite, testCase, limiter, barrier));
            lifeCycle.afterAll(state, testSuite);

        } catch (Throwable th) {
            log.warn("runTestCase stage: {}, testSuite: {} failed",
                    state.getStage(), state.getTestSuite(), th);
        } finally {
            log.debug("runTestCase ended. stage: {}, testSuite: {}", state.getStage(), state.getTestSuite());
            endLatch.countDown();
        }
    }


    private void runTestCase(String testName, T testCase, TestCaseMethod<T> test, Limiter limiter, Barrier barrier) {
        String threadName = Thread.currentThread().getName();
        AtomicLong invocations = new AtomicLong();
        BooleanSupplier checker = state.createChecker();
        barrier.testStart(testName);
        while (checker.getAsBoolean()) {
            if (!limiter.waitForPermit())
                throw new RuntimeException("Limiter await failed");

            long inv = invocations.getAndIncrement();
            Context context = new Context(state.getStage(), state.getTestSuite(), testName, threadName, inv, Instant.now());

            log.trace("runTest stage: {}, testSuite: {}, testCase: {}, inv: {}",
                    state.getStage(), state.getTestSuite(), testName, inv);

            lifeCycle.beforeEach(context, testCase);

            Clock clock = new Clock(record -> {
                sink.timeRecorded(context, record);

                if (!record.isSuccess())
                    state.incError();
            });

            Stopwatch stopwatch = clock.stopwatch(context.getTestCase() + "." + context.getTestSuite());
            try {
                // test
                test.run(testCase, clock);

                Record rec = stopwatch.success();
                sink.afterTestCase(context, rec.getElapsed(), null);
            } catch (Throwable th) {
                Record rec = stopwatch.fail(th);
                log.warn("runTest stage: {}, testSuite: {}, testCase: {}, inv: {} failed",
                        state.getStage(), state.getTestSuite(), testName, inv, th);

                sink.afterTestCase(context, rec.getElapsed(), rec.getError());
            }

            lifeCycle.afterEach(context, testCase);
        }

        barrier.testFinish(testName);
    }
}
