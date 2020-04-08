package ashes.of.bomber.runner;

import ashes.of.bomber.core.Iteration;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;
import ashes.of.bomber.delayer.Delayer;
import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.squadron.Barrier;
import ashes.of.bomber.stopwatch.Stopwatch;
import ashes.of.bomber.stopwatch.Tools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import javax.annotation.Nullable;
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

    @Nullable
    private volatile Object instance;

    public Worker(BlockingQueue<Runnable> queue, Thread thread) {
        this.queue = queue;
        this.thread = thread;
    }

    public WorkerState getState() {
        return state;
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


    private void run(Runnable task) {
        // a bit of busy-wait here, todo investigate
        for (int i = 0; i < 10000; i++) {
            boolean success = queue.offer(task);
            if (success)
                return;
        }

        throw new RuntimeException("Hey, you can't run task on " + thread.getName() + ". It's terrible situation and should be fixed");
    }

    public void runTestCase(TestSuite<Object> testSuite, TestCase<Object> testCase, Stage stage, Settings settings, RunnerState state,
                                CountDownLatch startLatch, CountDownLatch endLatch, Environment env, Sink sink, Barrier barrier) {
        this.state = new WorkerState(state);
        run(() -> runTestCase(testSuite, testCase, stage, settings, this.state, startLatch, endLatch, env, sink, barrier));
    }

    private void runTestCase(TestSuite<Object> testSuite, TestCase<Object> testCase, Stage stage, Settings settings, WorkerState state,
                                 CountDownLatch startLatch, CountDownLatch endLatch,
                                 Environment env, Sink sink, Barrier barrier) {

        ThreadContext.put("testSuite", testSuite.getName());
        ThreadContext.put("testCase", testCase.getName());
        ThreadContext.put("stage", stage.name());

        Limiter limiter = env.getLimiter().get();
        Delayer delayer = env.getDelayer().get();

        startLatch.countDown();
        try {
            log.trace("Await start testCase: {}", testCase.getName());
            if (!startLatch.await(60, SECONDS))
                log.warn("After 60s of waiting, worker started without all other workers (this isn't acceptable situation)");

        } catch (Throwable th) {
            log.warn("Run testCase: {} failed", testCase.getName(), th);
        }

        String threadName = Thread.currentThread().getName();

        testSuite.beforeCase(instance);

        log.trace("Try start testCase: {} -> barrier enter", testCase.getName());
        barrier.enterCase(stage, testSuite.getName(), testCase.getName());

        sink.beforeTestCase(stage, testSuite.getName(), testCase.getName(), state.getTestCaseStartTime(), settings);
        log.debug("Start testCase: {}", testCase.getName());

        state.startCaseIfNotStarted(testCase.getName(), stage, settings);
        BooleanSupplier checker = state.createChecker();
        while (checker.getAsBoolean()) {
            delayer.delay();

            if (!limiter.waitForPermit())
                throw new RuntimeException("Limiter await failed");

            Iteration it = new Iteration(state.nextItNumber(), stage, threadName, Instant.now(), testSuite.getName(), testCase.getName());

            testSuite.beforeEach(it, instance);

            Tools tools = new Tools(it, record -> {
                sink.timeRecorded(record);
                state.addCaughtCount(1);

                if (!record.isSuccess())
                    state.incError();
            });

            Stopwatch stopwatch = tools.stopwatch("");
            try {
                // test
                testCase.run(instance, tools);

                if (!testCase.isAsync())
                    stopwatch.success();

                long expected = tools.getStopwatchCount() - (testCase.isAsync() ? 1 : 0);
                state.addExpectedCount(expected);
                sink.afterEach(it, stopwatch.elapsed(), null);
            } catch (Throwable th) {
                if (!testCase.isAsync())
                    stopwatch.fail(th);

                sink.afterEach(it, stopwatch.elapsed(), th);
                log.trace("Call testCase: {} failed, it: {}", testCase.getName(), it, th);
            }

            testSuite.afterEach(it, instance);
        }

        log.debug("Test case finished, total its: {}, expected records: {}, caught records: {}, errors; {}",
                state.getCurrentIterationsCount(), state.getExpectedRecordsCount(), state.getCaughtRecordsCount(), state.getErrorsCount());

        try {
            int awaitCount = 10;
            long expectedCount = state.getExpectedRecordsCount();
            while (expectedCount > state.getCaughtRecordsCount() && awaitCount-- > 0) {
                log.debug("Oh, wait a second... caught records: {} is less than expected: {}",
                        state.getCaughtRecordsCount(), expectedCount);

                Thread.sleep(1000);
            }

            if (expectedCount != state.getCaughtRecordsCount()) {
                log.info("No more time to wait, not all records were fetched {} of {}. Results may be invalid.",
                        state.getCaughtRecordsCount(), expectedCount);
            }
        } catch (Exception e) {
            log.warn("Something is wrong", e);
        }

        log.trace("Try finish testCase: {} -> barrier leave", testCase.getName());
        barrier.leaveCase(stage, testSuite.getName(), testCase.getName());
        sink.afterTestCase(stage, testSuite.getName(), testCase.getName());
        state.finishCase();
        testSuite.afterCase(instance);
        log.debug("Finish testCase: {}", testCase.getName());

        endLatch.countDown();
        ThreadContext.clearAll();
    }

    public void runBeforeSuite(TestSuite<Object> testSuite, CountDownLatch latch) {
        run(() -> {
            if (this.instance != null)
                log.error("Worker contains instance, this is looks like bug");

            this.instance = testSuite.instance();
            testSuite.beforeSuite(instance);
            latch.countDown();
        });
    }

    public void runAfterSuite(TestSuite<Object> testSuite, CountDownLatch latch) {
        run(() -> {
            testSuite.afterSuite(instance);
            this.instance = null;
            latch.countDown();
        });
    }
}
