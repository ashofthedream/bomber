package ashes.of.bomber.runner;

import ashes.of.bomber.configuration.Configuration;
import ashes.of.bomber.core.TestCase;
import ashes.of.bomber.core.TestSuite;
import ashes.of.bomber.events.TestSuiteFinishedEvent;
import ashes.of.bomber.events.TestSuiteStartedEvent;
import ashes.of.bomber.plan.TestAppPlan;
import ashes.of.bomber.plan.TestCasePlan;
import ashes.of.bomber.configuration.Settings;
import ashes.of.bomber.flight.Stage;
import ashes.of.bomber.report.TestCaseReport;
import ashes.of.bomber.plan.TestSuitePlan;
import ashes.of.bomber.report.TestSuiteReport;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.squadron.Barrier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ashes.of.bomber.flight.Stage.IDLE;


public class Runner {
    private static final Logger log = LogManager.getLogger();

    private final String testApp;
    private final WorkerPool pool;
    private final Sink sink;
    private final List<TestSuite<?>> testSuites;

    public Runner(String testApp, WorkerPool pool, Sink sink, List<TestSuite<?>> testSuites) {
        this.testApp = testApp;
        this.pool = pool;
        this.sink = sink;
        this.testSuites = testSuites;
    }

    public List<TestSuiteReport> runTestApp(RunnerState state, long flightId, TestAppPlan testAppPlan) {
        Map<String, TestSuite<?>> suitesByName = testSuites.stream()
                .collect(Collectors.toMap(TestSuite::getName, suite -> suite));

        return testAppPlan.getTestSuites()
                .stream()
                .map(testSuitePlan -> {
                    log.debug("Try to run test suite: {}", testSuitePlan.getName());
                    TestSuite<Object> testSuite = (TestSuite<Object>) suitesByName.get(testSuitePlan.getName());
                    if (testSuite == null) {
                        log.warn("Test suite: {} not found", testSuitePlan.getName());
                        return new TestSuiteReport(testSuitePlan.getName(), List.of());
                    }

                    return runTestSuite(state, flightId, testSuitePlan, testSuite);
                })
                .collect(Collectors.toList());
    }

    /**
     * Runs the test case
     */
    public TestSuiteReport runTestSuite(RunnerState state, long flightId, TestSuitePlan plan, TestSuite<Object> testSuite) {
        ThreadContext.put("stage", IDLE.name());
        ThreadContext.put("testSuite", testSuite.getName());

        log.info("Run test suite: {}", testSuite.getName());

        log.trace("Reset before & after test suite lifecycle methods");
        testSuite.resetBeforeAndAfterSuite();

        state.startSuiteIfNotStarted(testSuite.getName());
        sink.beforeTestSuite(new TestSuiteStartedEvent(Instant.now(), flightId, testApp, testSuite.getName()));

        int threads = plan.getTestCases().stream()
                .mapToInt(testCasePlan -> determineWorkerThreadsCount(testSuite, testCasePlan))
                .max()
                .orElseThrow(() -> new RuntimeException("Can't determine thread count for test suite: " + testSuite.getName()));

        pool.acquire(threads);

        List<TestCaseReport> testCaseReports = new ArrayList<>();
        try {
            awaitBeforeSuite(testSuite);

            plan.getTestCases()
                    .forEach(testCasePlan -> {
                        var testCase = testSuite.getTestCase(testCasePlan.getName());
                        if (testCase == null) {
                            log.warn("Test case: {} not found in test suite: {}, but it exists in the plan",
                                    testCasePlan.getName(), testSuite.getName());
                            return;
                        }

                        log.trace("Reset before & after test case lifecycle methods");
                        testSuite.resetBeforeAndAfterCase();

                        ThreadContext.put("testCase", testCasePlan.getName());
                        log.debug("Run test case: {}", testCasePlan.getName());
                        var config = Optional.ofNullable(testCasePlan.getConfiguration());
                        Settings warmUp = config
                                .map(Configuration::getWarmUp)
                                .orElse(testCase.getConfiguration().getWarmUp());

                        Settings settings = config.map(Configuration::getSettings)
                                .orElse(testCase.getConfiguration()
                                        .getSettings());

                        if (!warmUp.isDisabled()) {
                            runTestCase(state, flightId, testApp, testSuite, testCase, Stage.WARM_UP, warmUp);
                        }

                        var report = runTestCase(state, flightId, testApp, testSuite, testCase, Stage.TEST, settings);
                        testCaseReports.add(report);
                        ThreadContext.remove("testCase");
                    });

            awaitAfterSuite(testSuite);

        } catch (Throwable throwable) {
            log.error("Run test suite: {} failed", testSuite.getName(), throwable);
        }

        pool.releaseAll();

        sink.afterTestSuite(new TestSuiteFinishedEvent(Instant.now(), flightId, testApp, testSuite.getName()));
        state.finishSuite();
        ThreadContext.clearAll();

        return new TestSuiteReport(testSuite.getName(), testCaseReports);
    }

    private int determineWorkerThreadsCount(TestSuite<Object> testSuite, TestCasePlan testCasePlan) {
        var warmUp = Optional.ofNullable(testCasePlan.getConfiguration())
                .map(Configuration::getWarmUp)
                .stream();

        var settings = Optional.ofNullable(testCasePlan.getConfiguration())
                .map(Configuration::getSettings)
                .stream();

        return Stream.concat(warmUp, settings)
                .mapToInt(Settings::getThreadsCount)
                .max()
                .orElseGet(() -> {
                    var testCase = testSuite.getTestCase(testCasePlan.getName());
                    if (testCase == null) {
                        log.warn("Test case: {} not found in test suite: {}, but it exists in the plan",
                                testCasePlan.getName(), testSuite.getName());
                        return 0;
                    }

                    var config = testCase.getConfiguration();
                    return Math.max(
                            config.getWarmUp().getThreadsCount(),
                            config.getSettings().getThreadsCount()
                    );
                });
    }

    private void awaitBeforeSuite(TestSuite<Object> testSuite) throws InterruptedException {
        log.debug("Call beforeSuite for all workers: {}", pool.getAcquired().size());
        CountDownLatch beforeSuiteLatch = new CountDownLatch(pool.getAcquired().size());
        pool.getAcquired().forEach(worker ->
                worker.runBeforeSuite(testSuite, beforeSuiteLatch));

        log.debug("Await workers beforeSuite");
        beforeSuiteLatch.await();
    }

    private void awaitAfterSuite(TestSuite<Object> testSuite) throws InterruptedException {
        log.debug("Call afterSuite for all workers: {}", pool.getAcquired().size());
        CountDownLatch afterSuiteLatch = new CountDownLatch(pool.getAcquired().size());
        pool.getAcquired().forEach(worker ->
                worker.runAfterSuite(testSuite, afterSuiteLatch));

        log.debug("Await workers afterSuite");
        afterSuiteLatch.await();
    }

    private TestCaseReport runTestCase(RunnerState state, long flightId, String testApp, TestSuite<Object> testSuite, TestCase<Object> testCase, Stage stage, Settings settings) {
        ThreadContext.put("stage", stage.name());
        log.info("Start stage: {}", stage);
        state.startCaseIfNotStarted(testCase.getName(), stage, settings);

        Barrier barrier = testCase.getConfiguration().getBarrier()
                .workers(settings.getThreadsCount())
                .build();

        CountDownLatch startLatch = new CountDownLatch(settings.getThreadsCount());
        CountDownLatch finishLatch = new CountDownLatch(settings.getThreadsCount());

        log.debug("Run {} workers", settings.getThreadsCount());

        pool.getAcquired()
                .stream()
                .limit(settings.getThreadsCount())
                .forEach(worker -> worker.runTestCase(state, flightId, testApp, testSuite, testCase, stage, settings, startLatch, finishLatch, sink, barrier));

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

        ThreadContext.put("stage", stage.name());

        return new TestCaseReport(testCase.getName(), settings,
                settings.getTotalIterationsCount() - state.getTotalIterationsRemain(),
                state.getErrorCount(),
                state.getCaseElapsedTime()
        );
    }
}
