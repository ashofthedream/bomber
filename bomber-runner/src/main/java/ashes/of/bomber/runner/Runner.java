package ashes.of.bomber.runner;

import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;
import ashes.of.bomber.sink.AsyncSink;
import ashes.of.bomber.sink.MultiSink;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.squadron.Barrier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;

import static ashes.of.bomber.core.Stage.Idle;


public class Runner {
    private static final Logger log = LogManager.getLogger();

    private final WorkerPool pool;
    private final Sink sink;

    private final Map<String, Worker> workers = new ConcurrentSkipListMap<>();

    public Runner(WorkerPool pool, List<Sink> sinks) {
        this.pool = pool;
        this.sink = new AsyncSink(new MultiSink(sinks));
    }

    /**
     * Runs the test case
     */
    public void startTestCase(Environment env, RunnerState state, TestSuite<Object> testSuite, List<String> testCases) {
        ThreadContext.put("stage", Idle.name());
        ThreadContext.put("testSuite", testSuite.getName());

        log.info("Start test suite: {} with warm up: {} and test settings: {}",
                testSuite.getName(), testSuite.getWarmUp(), testSuite.getSettings());

        log.trace("Reset before & after test suite lifecycle methods");
        testSuite.resetBeforeAndAfterSuite();

        state.startSuiteIfNotStarted(testSuite.getName());
        sink.beforeTestSuite(testSuite.getName(), Instant.now());

        int threads = testCases.stream()
                .map(testSuite::getTestCase)
                .mapToInt(testCase -> Math.max(
                        testCase.getWarmUp().getThreadsCount(),
                        testCase.getLoadTest().getThreadsCount()
                ))
                .max()
                .orElse(0);

        log.debug("Acquire {} workers", threads);
        for (int t = 0; t < threads; t++) {
            Worker worker = pool.acquire();
            workers.put(worker.getName(), worker);
        }

        try {
            awaitBeforeSuite(testSuite);

            testCases.stream()
                    .map(testSuite::getTestCase)
                    .forEach(testCase -> {
                        log.trace("Reset before & after test case lifecycle methods");
                        testSuite.resetBeforeAndAfterCase();

                        ThreadContext.put("testCase", testCase.getName());
                        log.debug("Run test case: {}", testCase.getName());

                        Settings warmUp = testCase.getWarmUp();
                        if (!warmUp.isDisabled()) {
                            startTestCase(env, state, testSuite, testCase, Stage.WarmUp, warmUp);
                        }

                        startTestCase(env, state, testSuite, testCase, Stage.Test, testCase.getLoadTest());

                        ThreadContext.remove("testCase");
                    });

            awaitAwaitSuite(testSuite);

        } catch (Throwable throwable) {
            log.error("Run test suite: {} failed", testSuite.getName(), throwable);
        }

        log.debug("Release all workers: {}", workers.size());
        pool.release(workers.values());
        workers.clear();

        sink.afterTestSuite(testSuite.getName());
        state.finishSuite();
        ThreadContext.clearAll();
    }

    private void awaitBeforeSuite(TestSuite<Object> testSuite) throws InterruptedException {
        log.debug("Call beforeSuite for all workers: {}", workers.size());
        CountDownLatch beforeSuiteLatch = new CountDownLatch(workers.size());
        workers.forEach((name, worker) ->
                worker.runBeforeSuite(testSuite, beforeSuiteLatch));

        log.debug("Await workers beforeSuite");
        beforeSuiteLatch.await();
    }

    private void awaitAwaitSuite(TestSuite<Object> testSuite) throws InterruptedException {
        log.debug("Call afterSuite for all workers: {}", workers.size());
        CountDownLatch afterSuiteLatch = new CountDownLatch(workers.size());
        workers.forEach((name, worker) ->
                worker.runAfterSuite(testSuite, afterSuiteLatch));

        log.debug("Await workers afterSuite");
        afterSuiteLatch.await();
    }

    public void startTestCase(Environment env, RunnerState state, TestSuite<Object> testSuite, TestCase<Object> testCase, Stage stage, Settings settings) {
        ThreadContext.put("stage", stage.name());
        log.info("Start stage: {}", stage);
        state.startCaseIfNotStarted(testCase.getName(), stage, settings);

        Barrier barrier = env.getBarrier()
                .workers(settings.getThreadsCount())
                .build();

        CountDownLatch startLatch = new CountDownLatch(settings.getThreadsCount());
        CountDownLatch finishLatch = new CountDownLatch(settings.getThreadsCount());

        log.debug("Run {} workers", settings.getThreadsCount());

        workers.values()
                .stream()
                .limit(settings.getThreadsCount())
                .forEach(worker -> worker.runTestCase(testSuite, testCase, stage, settings, state, startLatch, finishLatch, env, sink, barrier));

        try {
            log.debug("Await end of stage: {}", stage);
            finishLatch.await();

            log.debug("All workers done, 1s cooldown");
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            log.error("We've been interrupted", e);
        }

        log.info("Finish stage: {}, elapsed time: {}ms", stage, state.getCaseElapsedTime());
        state.finishCase();

        ThreadContext.remove("stage");
    }
}
