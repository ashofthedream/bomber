package ashes.of.bomber.runner;

import ashes.of.bomber.core.Iteration;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.delayer.Delayer;
import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.squadron.Barrier;
import ashes.of.bomber.stopwatch.Stopwatch;
import ashes.of.bomber.stopwatch.Tools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.function.BooleanSupplier;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Worker {
    private static final Logger log = LogManager.getLogger();

    private final BlockingQueue<Runnable> queue;
    private final Thread thread;

    private volatile WorkerState state;
    private volatile Settings settings;
    private volatile CountDownLatch startLatch;
    private volatile CountDownLatch endLatch;
    private volatile Barrier barrier;
    private volatile Environment env;
    private volatile Sink sink;


    public Worker(BlockingQueue<Runnable> queue, Thread thread) {
        this.queue = queue;
        this.thread = thread;
    }

    public WorkerState getState() {
        return state;
    }

    public <T> void run(State state, Settings settings, CountDownLatch begin, CountDownLatch end, Barrier barrier, Environment env, Sink sink, LifeCycle<T> lifeCycle) {
        this.state = new WorkerState(settings, state);
        this.settings = settings;
        this.startLatch = begin;
        this.endLatch = end;
        this.barrier = barrier;
        this.env = env;
        this.sink = sink;

        run(() -> runTestSuite(lifeCycle));
    }

    private void run(Runnable task) {
        // a bit of busy-wait here, todo investigate
        for (int i = 0; i < 10000; i++) {
            boolean success = queue.offer(task);
            if (success)
                return;
        }

        throw new RuntimeException("Hey, you can't run task on " + thread.getName() + ". It's terrible situation and should be fixed");
    }

    public String getName() {
        return thread.getName();
    }

    public boolean isActive() {
        return thread.isAlive();
    }

    public void stop() {
        thread.stop();
    }


    private <T> void runTestSuite(LifeCycle<T> lifeCycle) {
        ThreadContext.put("stage", state.getStage().name());
        ThreadContext.put("testSuite", state.getTestSuite());
        log.debug("run test suite");
        try {
            T instance = lifeCycle.instance();

            startLatch.countDown();

            log.trace("await start");
            if (!startLatch.await(60, SECONDS))
                log.warn("after 60s of waiting, worker started without all other workers (this isn't acceptable situation)");

            lifeCycle.beforeAll(instance);
            lifeCycle.testCases()
                    .forEach((name, testCase) -> runTestCase(instance, testCase, lifeCycle));
            lifeCycle.afterAll(instance);

        } catch (Throwable th) {
            log.warn("run test suite failed", th);
        }

        log.debug("finish test suite");
        endLatch.countDown();
        ThreadContext.clearAll();
    }


    private <T> void runTestCase(T instance, TestCase<T> testCase, LifeCycle<T> lifeCycle) {
        Limiter limiter = env.getLimiter().get();
        Delayer delayer = env.getDelayer().get();

        ThreadContext.put("testCase", testCase.getName());
        String threadName = Thread.currentThread().getName();


        log.trace("run test case, enter");
        barrier.enterCase(state.getStage(), state.getTestSuite(), testCase.getName());
        state.startCaseIfNotStarted(testCase.getName());
        sink.beforeTestCase(state.getStage(), state.getTestSuite(), testCase.getName(), state.getTestCaseStartTime(), settings);
        log.debug("run test case");

        BooleanSupplier checker = state.createChecker();
        while (checker.getAsBoolean()) {
            delayer.delay();

            if (!limiter.waitForPermit())
                throw new RuntimeException("Limiter await failed");

            Iteration it = new Iteration(state.nextItNumber(), state.getStage(), threadName, Instant.now(), state.getTestSuite(), testCase.getName());

            lifeCycle.beforeEach(it, instance);

            Tools tools = new Tools(it, record -> {
                sink.timeRecorded(record);

                if (!record.isSuccess())
                    state.incError();
            });

            Stopwatch stopwatch = tools.stopwatch("");
            try {
                // test
                testCase.getMethod().run(instance, tools);

                if (!testCase.isAsync())
                    stopwatch.success();

                sink.afterEach(it, stopwatch.elapsed(), null);
            } catch (Throwable th) {
                if (!testCase.isAsync())
                    stopwatch.fail(th);

                sink.afterEach(it, stopwatch.elapsed(), th);
                log.trace("{} | runTestCase failed, it: {}", state, it, th);
            }

            lifeCycle.afterEach(it, instance);
        }

        log.trace("finish test case, leave");
        barrier.leaveCase(state.getStage(), state.getTestSuite(), testCase.getName());
        sink.afterTestCase(state.getStage(), state.getTestSuite(), testCase.getName());
        state.finishCase();
        log.debug("finish test case");
    }

}
