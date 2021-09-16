package ashes.of.bomber.runner;

import ashes.of.bomber.core.TestApp;
import ashes.of.bomber.core.TestCase;
import ashes.of.bomber.core.TestSuite;
import ashes.of.bomber.snapshots.WorkerSnapshot;
import ashes.of.bomber.events.TestCaseAfterEachEvent;
import ashes.of.bomber.events.TestCaseBeforeEachEvent;
import ashes.of.bomber.events.TestCaseFinishedEvent;
import ashes.of.bomber.events.TestCaseStartedEvent;
import ashes.of.bomber.flight.Iteration;
import ashes.of.bomber.configuration.Settings;
import ashes.of.bomber.delayer.Delayer;
import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.squadron.Barrier;
import ashes.of.bomber.tools.Stopwatch;
import ashes.of.bomber.tools.Tools;
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
    private volatile Object context;

    public Worker(BlockingQueue<Runnable> queue, Thread thread) {
        this.queue = queue;
        this.thread = thread;
    }

    public String getName() {
        return thread.getName();
    }

    public WorkerSnapshot getSnapshot() {
        return new WorkerSnapshot(getName(),
                state.getCurrentIterationsCount(),
                state.getRemainIterationsCount(),
                state.getErrorsCount(),
                state.getExpectedRecordsCount(),
                state.getCaughtRecordsCount()
        );
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


    public void runBeforeSuite(TestSuite<Object> testSuite, CountDownLatch latch) {
        run(() -> {
            if (this.context != null)
                log.error("Worker contains instance, this is looks like bug");

            this.context = testSuite.getContext();
            testSuite.beforeSuite(context);
            latch.countDown();
        });
    }

    public void runTestCase(RunnerState state, long flightId, TestApp testApp, TestSuite<Object> testSuite, TestCase<Object> testCase, Settings settings,
                            CountDownLatch startLatch, CountDownLatch endLatch, Sink sink) {
        this.state = new WorkerState(state, startLatch, endLatch, sink);
        run(() -> runTestCase(flightId, testApp, testSuite, testCase, settings));
    }

    private void runTestCase(long flightId, TestApp testApp, TestSuite<Object> testSuite, TestCase<Object> testCase, Settings settings) {
        ThreadContext.put("flightId", String.valueOf(flightId));
        ThreadContext.put("testApp", testApp.getName());
        ThreadContext.put("testSuite", testSuite.getName());
        ThreadContext.put("testCase", testCase.getName());


        Limiter limiter = testCase.getConfiguration().getLimiter().build();
        Delayer delayer = testCase.getConfiguration().getDelayer().build();

        // todo it's not working, ha-ha
        Barrier barrier = testCase.getConfiguration().getBarrier().build();

        var startLatch = state.getStartLatch();
        startLatch.countDown();

        try {
            log.trace("Await start test case: {}", testCase.getName());
            if (!startLatch.await(60, SECONDS))
                log.warn("After 60s of waiting, worker started without all other workers (this isn't acceptable situation)");

        } catch (Throwable th) {
            log.warn("Run testCase: {} failed", testCase.getName(), th);
        }

        String threadName = Thread.currentThread().getName();

        testSuite.beforeCase(context);

        var runnerState = state.getRunnerState();
        var sink = state.getSink();

        log.trace("Try start testCase: {} -> barrier enter", testCase.getName());
        barrier.enterCase(testApp.getName(), testSuite.getName(), testCase.getName());

        if (runnerState.needCallSinkBeforeTestCase())
            sink.beforeTestCase(new TestCaseStartedEvent(state.getTestCaseStartTime(), flightId, testApp.getName(), testSuite.getName(), testCase.getName(), settings));

        log.debug("Start testCase: {}", testCase.getName());

        state.startCaseIfNotStarted(testCase.getName(), settings);
        BooleanSupplier checker = state.createChecker();
        while (checker.getAsBoolean()) {
            delayer.delay();

            if (!limiter.waitForPermit())
                throw new RuntimeException("Limiter await failed");

            Iteration it = new Iteration(flightId, state.nextIterationNumber(), threadName, Instant.now(), testApp.getName(), testSuite.getName(), testCase.getName());
            if (runnerState.needUpdate()) {
                var remainIt = runnerState.getTotalIterationsRemain();
                log.debug("Current progress. iterations count: {}, remain count: {}, errors count: {}, remain time: {}ms",
                        settings.getTotalIterationsCount() - remainIt, remainIt, runnerState.getErrorCount(), runnerState.getCaseRemainTime());
            }

            testSuite.beforeEach(it, context);
            sink.beforeEach(new TestCaseBeforeEachEvent(it.getTimestamp(), flightId, testApp.getName(), testSuite.getName(), testCase.getName()));
            Tools tools = new Tools(it, record -> {
                sink.timeRecorded(record);
                state.addCaughtCount(1);

                if (!record.isSuccess())
                    state.incError();
            });

            Stopwatch stopwatch = tools.stopwatch("");
            try {
                // test
                testCase.run(context, tools);

                if (!testCase.isAsync())
                    stopwatch.success();

                long expected = tools.getStopwatchCount() - (testCase.isAsync() ? 1 : 0);
                state.addExpectedCount(expected);
                sink.afterEach(new TestCaseAfterEachEvent(it.getTimestamp(), flightId, testApp.getName(), testSuite.getName(), testCase.getName(), threadName, it.getNumber(), stopwatch.elapsed(), null));
            } catch (Throwable th) {
                if (!testCase.isAsync())
                    stopwatch.fail(th);

                sink.afterEach(new TestCaseAfterEachEvent(it.getTimestamp(), flightId, testApp.getName(), testSuite.getName(), testCase.getName(), threadName, it.getNumber(), stopwatch.elapsed(), th));
                log.warn("Call testCase: {} failed, it: {}", testCase.getName(), it, th);
            }

            testSuite.afterEach(it, context);
        }

        log.debug("Test case finished, total its: {}, expected records: {}, caught records: {}, errors; {}",
                state.getCurrentIterationsCount(), state.getExpectedRecordsCount(), state.getCaughtRecordsCount(), state.getErrorsCount());

        try {
            // just wait a second, for async events
            log.debug("Just, wait a second... sink may receive some async events");
            Thread.sleep(1000);

            int awaitCount = 14;
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
        barrier.leaveCase(testApp.getName(), testSuite.getName(), testCase.getName());
        if (runnerState.needCallSinkAfterTestCase())
            sink.afterTestCase(new TestCaseFinishedEvent(Instant.now(), flightId, testApp.getName(), testSuite.getName(), testCase.getName()));
        state.finishCase();
        testSuite.afterCase(context);
        log.debug("Finish testCase: {}", testCase.getName());

        state.getEndLatch().countDown();
        ThreadContext.clearAll();
    }

    public void runAfterSuite(TestSuite<Object> testSuite, CountDownLatch latch) {
        run(() -> {
            testSuite.afterSuite(context);
            this.context = null;
            latch.countDown();
        });
    }
}
