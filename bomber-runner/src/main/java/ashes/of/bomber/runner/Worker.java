package ashes.of.bomber.runner;

import ashes.of.bomber.core.Test;
import ashes.of.bomber.core.TestSuite;
import ashes.of.bomber.events.EventMachine;
import ashes.of.bomber.snapshots.WorkerSnapshot;
import ashes.of.bomber.events.TestCaseAfterEachEvent;
import ashes.of.bomber.events.TestCaseBeforeEachEvent;
import ashes.of.bomber.delayer.Delayer;
import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.squadron.Barrier;
import ashes.of.bomber.tools.Stopwatch;
import ashes.of.bomber.tools.Tools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import javax.annotation.Nullable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.function.BooleanSupplier;

public class Worker {
    private static final Logger log = LogManager.getLogger();

    private final EventMachine em;
    private final BlockingQueue<Runnable> queue;
    private final Thread thread;

    private volatile WorkerState state;

    @Nullable
    private volatile Object context;

    public Worker(EventMachine em, BlockingQueue<Runnable> queue, Thread thread) {
        this.em = em;
        this.queue = queue;
        this.thread = thread;
    }

    public String getName() {
        return thread.getName();
    }

    public WorkerSnapshot getSnapshot() {
        return new WorkerSnapshot(getName(),
                state.getIterationsCount(),
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


    public void finish() {
        var state = this.state;
        if (state != null) {
            state.markFinished();
        }
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
                log.error("Worker has context, this is looks like bug");

            this.context = testSuite.getContext();
            testSuite.beforeSuite(context);
            latch.countDown();
        });
    }

    public void run(TestCaseState state) {
        this.state = new WorkerState(state);
        run(() -> run(this.state));
    }

    private void run(WorkerState state) {
        state.getParent().getFinishLatch().register();
        var parent = state.getParent();
        var testCase = parent.getTestCase();
        var testSuite = parent.getSuiteState().getTestSuite();
        var testApp = parent.getSuiteState().getAppState().getTestApp();

        ThreadContext.put("flightId", String.valueOf(parent.getFlightId()));
        ThreadContext.put("testApp", testApp.getName());
        ThreadContext.put("testSuite", testSuite.getName());
        ThreadContext.put("testCase", testCase.getName());


        var config = parent.getConfiguration();
        Limiter limiter = config.limiter().get();
        Delayer delayer = config.delayer().get();
        Barrier barrier = config.barrier().get();

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

            if (!limiter.await())
                throw new RuntimeException("Limiter await failed");

            if (parent.needUpdate()) {
                var totalCount = parent.getIterationsCount();
                var settings = config.settings().get();
                log.debug("Current progress. total iterations count: {}, remain count: {}, errors count: {}, remain time: {}ms",
                        totalCount,
                        settings.iterations() - totalCount,
                        parent.getErrorCount(),
                        settings.duration().toMillis() - (System.currentTimeMillis() - parent.getStartTime().toEpochMilli())
                );
            }

            var it = state.createIteration();
            testSuite.beforeEach(it, context);

            em.dispatch(new TestCaseBeforeEachEvent(it.timestamp(), it.flightId(), it.test()));

            Tools tools = new Tools(it, record -> {
                em.dispatch(record);
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
                em.dispatch(new TestCaseAfterEachEvent(
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

                em.dispatch(new TestCaseAfterEachEvent(
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

    public void runAfterSuite(TestSuite<Object> testSuite, CountDownLatch latch) {
        run(() -> {
            testSuite.afterSuite(context);
            this.context = null;
            latch.countDown();
        });
    }
}
