package ashes.of.bomber.runner;

import ashes.of.bomber.configuration.Settings;
import ashes.of.bomber.core.Test;
import ashes.of.bomber.core.TestSuite;
import ashes.of.bomber.snapshots.WorkerSnapshot;
import ashes.of.bomber.events.TestCaseAfterEachEvent;
import ashes.of.bomber.events.TestCaseBeforeEachEvent;
import ashes.of.bomber.delayer.Delayer;
import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.squadron.Barrier;
import ashes.of.bomber.tools.Stopwatch;
import ashes.of.bomber.tools.Tools;
import ashes.of.bomber.watcher.Watcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.function.BooleanSupplier;

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
                state.getIterationsCount(),
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

    public void run(TestCaseState state, Sink sink, List<Watcher> watchers) {
        this.state = new WorkerState(state);
        run(() -> run(this.state, sink, watchers));
    }

    private void run(WorkerState state, Sink sink, List<Watcher> watchers) {
        var parent = state.getParent();
        var testCase = parent.getTestCase();
        var testSuite = parent.getParent().getTestSuite();
        var testApp = parent.getParent().getParent().getTestApp();

        ThreadContext.put("flightId", String.valueOf(parent.getFlightId()));
        ThreadContext.put("testApp", testApp.getName());
        ThreadContext.put("testSuite", testSuite.getName());
        ThreadContext.put("testCase", testCase.getName());


        Limiter limiter = parent.getConfiguration().limiter().build();
        Delayer delayer = parent.getConfiguration().delayer().build();

        // todo it's not working, ha-ha
        Barrier barrier = parent.getConfiguration().barrier().build();

        state.start();

        try {
            log.trace("Await other workers to start test case: {}", testCase.getName());
            if (!state.awaitStart(60))
                log.warn("After 60s of waiting, worker started without all other workers (this isn't acceptable situation)");

        } catch (Throwable th) {
            log.warn("Run testCase: {} failed", testCase.getName(), th);
        }

        testSuite.beforeCase(context);

        var test = new Test(testApp.getName(), testSuite.getName(), testCase.getName());
        log.trace("Try start test case -> barrier enter");
        barrier.enterCase(test);

        log.info("Start test case: {}", testCase.getName());

        BooleanSupplier condition = state.createCondition();
        while (condition.getAsBoolean()) {
            delayer.delay();

            if (!limiter.waitForPermit())
                throw new RuntimeException("Limiter await failed");

            if (parent.needUpdate()) {
                var totalCount = parent.getTotalIterationsCount();
                var settings = parent.getConfiguration().settings();
                log.debug("Current progress. total iterations count: {}, remain count: {}, errors count: {}, remain time: {}ms",
                        totalCount,
                        settings.totalIterationsCount() - totalCount,
                        parent.getErrorCount(),
                        settings.duration().toMillis() - (System.currentTimeMillis() - parent.getStartTime().toEpochMilli())
                );
            }

            var it = state.createIteration();
            testSuite.beforeEach(it, context);

            send(sink, watchers, new TestCaseBeforeEachEvent(
                    it.timestamp(),
                    it.flightId(),
                    it.test()
            ));

            Tools tools = new Tools(it, record -> {
                sink.timeRecorded(record);
                state.addCaughtCount(1);

                if (!record.success())
                    state.addError();
            });

            Stopwatch stopwatch = tools.stopwatch();
            try {
                // test
                testCase.run(context, tools);

                if (!testCase.isAsync())
                    stopwatch.success();

                long expected = tools.getStopwatchCount() - (testCase.isAsync() ? 1 : 0);
                state.addExpectedCount(expected);
                send(sink, watchers, new TestCaseAfterEachEvent(
                        it.timestamp(),
                        it.flightId(),
                        it.test(),
                        it.thread(),
                        it.number(),
                        stopwatch.elapsed(),
                        null
                ));
            } catch (Throwable th) {
                if (!testCase.isAsync())
                    stopwatch.fail(th);

                send(sink, watchers, new TestCaseAfterEachEvent(
                        it.timestamp(),
                        it.flightId(),
                        it.test(),
                        it.thread(),
                        it.number(),
                        stopwatch.elapsed(),
                        th
                ));

                log.warn("Call test case: {} failed, it: {}", testCase.getName(), it, th);
            }

            testSuite.afterEach(it, context);
        }

        log.debug("Test case finished, total its: {}, expected records: {}, caught records: {}, errors; {}",
                state.getIterationsCount(), state.getExpectedRecordsCount(), state.getCaughtRecordsCount(), state.getErrorsCount());

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

        log.trace("Try finish test case -> barrier leave");
        barrier.leaveCase(test);

        testSuite.afterCase(context);

        log.debug("Finish test case: {}", testCase.getName());
        state.finish();
        ThreadContext.clearAll();
    }

    private void send(Sink sink, List<Watcher> watchers, TestCaseBeforeEachEvent event) {
        sink.beforeEach(event);
        watchers.forEach(watcher -> watcher.beforeEach(event));
    }

    private void send(Sink sink, List<Watcher> watchers, TestCaseAfterEachEvent event) {
        sink.afterEach(event);
        watchers.forEach(watcher -> watcher.afterEach(event));
    }

    public void runAfterSuite(TestSuite<Object> testSuite, CountDownLatch latch) {
        run(() -> {
            testSuite.afterSuite(context);
            this.context = null;
            latch.countDown();
        });
    }
}
