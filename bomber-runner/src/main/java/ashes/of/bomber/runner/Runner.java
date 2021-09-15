package ashes.of.bomber.runner;

import ashes.of.bomber.configuration.Configuration;
import ashes.of.bomber.configuration.Settings;
import ashes.of.bomber.configuration.Stage;
import ashes.of.bomber.core.TestApp;
import ashes.of.bomber.core.TestCase;
import ashes.of.bomber.core.TestSuite;
import ashes.of.bomber.events.TestAppFinishedEvent;
import ashes.of.bomber.events.TestAppStartedEvent;
import ashes.of.bomber.events.TestSuiteFinishedEvent;
import ashes.of.bomber.events.TestSuiteStartedEvent;
import ashes.of.bomber.flight.plan.TestAppPlan;
import ashes.of.bomber.flight.plan.TestCasePlan;
import ashes.of.bomber.flight.plan.TestFlightPlan;
import ashes.of.bomber.flight.plan.TestSuitePlan;
import ashes.of.bomber.flight.report.TestAppReport;
import ashes.of.bomber.flight.report.TestCaseReport;
import ashes.of.bomber.flight.report.TestFlightReport;
import ashes.of.bomber.flight.report.TestSuiteReport;
import ashes.of.bomber.sink.AsyncSink;
import ashes.of.bomber.sink.MultiSink;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.snapshots.FlightSnapshot;
import ashes.of.bomber.snapshots.WorkerSnapshot;
import ashes.of.bomber.squadron.Barrier;
import ashes.of.bomber.threads.BomberThreadFactory;
import ashes.of.bomber.watcher.Watcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ashes.of.bomber.configuration.Stage.IDLE;


public class Runner {
    private static final Logger log = LogManager.getLogger();

    private final WorkerPool pool = new WorkerPool();
    private final RunnerState state;
    private final List<Sink> sinks;
    private final Sink sink;
    private final List<Watcher> watchers;
    private final List<TestApp> apps;

    public Runner(RunnerState state, List<Sink> sinks, List<Watcher> watchers, List<TestApp> apps) {
        this.state = state;
        this.sinks = sinks;
        this.sink = new AsyncSink(new MultiSink(sinks));
        this.watchers = watchers;
        this.apps = apps;
    }

    public TestFlightReport run(TestFlightPlan flightPlan) {
        log.info("Start flight: {}", flightPlan.getFlightId());
        Instant flightStartTime = Instant.now();

        var appsByName = apps.stream()
                .collect(Collectors.toMap(TestApp::getName, app -> app));

        var reports = flightPlan.getTestApps().stream()
                .map(testAppPlan -> {
                    var testApp = appsByName.get(testAppPlan.getName());
                    if (testApp == null) {
                        log.warn("Bomber has no app with name: {}", testAppPlan.getName());
                        return null;
                    }

                    try {
                        return runTestApp(flightPlan.getFlightId(), testAppPlan, testApp);
                    } catch (Throwable th) {
                        log.error("Unexpected throwable", th);
                        throw th;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


        Instant flightFinishTime = Instant.now();


        return new TestFlightReport(flightPlan, flightStartTime, flightFinishTime, reports);
    }

    public TestAppReport runTestApp(long flightId, TestAppPlan testAppPlan, TestApp testApp) {
        ThreadContext.put("bomberApp", testApp.getName());
        ThreadContext.put("flightId", String.valueOf(flightId));

        log.info("Start application: {} flight: {}", testApp.getName(), flightId);
        testAppPlan.getTestSuites()
                .forEach(testSuite -> {
                    log.debug("Planned test suite: {}", testSuite.getName());
                    testSuite.getTestCases().forEach(testCase -> {
                        log.debug("Planned test case: {}.{}, settings: {}", testSuite.getName(), testCase.getName(),
                                Optional.ofNullable(testCase.getConfiguration())
                                        .map(Configuration::getSettings)
                                        .orElse(null));
                    });
                });

        Instant startTime = Instant.now();

        sendAppStartedEvent(new TestAppStartedEvent(startTime, flightId, testApp.getName()));

        log.debug("Start watchers, watch every {}s", 1);
        ScheduledExecutorService watcherEx = Executors.newSingleThreadScheduledExecutor(BomberThreadFactory.watcher());
        List<ScheduledFuture<?>> wfs = watchers.stream()
                .map(watcher -> watcherEx.scheduleAtFixedRate(() -> watcher.watch(getState()), 0, 1, TimeUnit.SECONDS))
                .collect(Collectors.toList());

        try {
            Map<String, TestSuite<?>> suitesByName = testApp.getTestSuites()
                    .stream()
                    .collect(Collectors.toMap(TestSuite::getName, suite -> suite));

            var testSuiteReports = testAppPlan.getTestSuites()
                    .stream()
                    .map(testSuitePlan -> {
                        log.debug("Try to run test suite: {}", testSuitePlan.getName());
                        TestSuite<Object> testSuite = (TestSuite<Object>) suitesByName.get(testSuitePlan.getName());
                        if (testSuite == null) {
                            log.warn("Test suite: {} not found", testSuitePlan.getName());
                            return new TestSuiteReport(testSuitePlan.getName(), List.of());
                        }

                        return runTestSuite(state, flightId, testSuitePlan, testApp, testSuite);
                    })
                    .collect(Collectors.toList());

            log.info("Finish application: {} flight: {}", testApp.getName(), flightId);
            Instant finishTime = Instant.now();

            sendAppFinishedEvent(new TestAppFinishedEvent(finishTime, flightId, testApp.getName()));

            return new TestAppReport(testAppPlan, testApp.getName(), startTime, finishTime, testSuiteReports);
        } finally {
            log.debug("Shutdown for each sink and watcher");

            pool.shutdown();
            wfs.forEach(wf -> wf.cancel(false));
            watcherEx.shutdown();

            ThreadContext.clearAll();
        }
    }

    /**
     * Runs the test case
     */
    public TestSuiteReport runTestSuite(RunnerState state, long flightId, TestSuitePlan plan, TestApp testApp, TestSuite<Object> testSuite) {
        ThreadContext.put("stage", IDLE.name());
        ThreadContext.put("testSuite", testSuite.getName());

        log.info("Run test suite: {}", testSuite.getName());

        log.trace("Reset before & after test suite lifecycle methods");
        testSuite.resetBeforeAndAfterSuite();

        state.startSuiteIfNotStarted(testSuite.getName());
        sink.beforeTestSuite(new TestSuiteStartedEvent(Instant.now(), flightId, testApp.getName(), testSuite.getName()));

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
                            runTestCase(state, flightId, testApp.getName(), testSuite, testCase, Stage.WARM_UP, warmUp);
                        }

                        var report = runTestCase(state, flightId, testApp.getName(), testSuite, testCase, Stage.TEST, settings);
                        testCaseReports.add(report);
                        ThreadContext.remove("testCase");
                    });

            awaitAfterSuite(testSuite);

        } catch (Throwable throwable) {
            log.error("Run test suite: {} failed", testSuite.getName(), throwable);
        }

        pool.releaseAll();

        sink.afterTestSuite(new TestSuiteFinishedEvent(Instant.now(), flightId, testApp.getName(), testSuite.getName()));
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

        Barrier barrier = testCase.getConfiguration().getBarrier().build();

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



    private void sendAppStartedEvent(TestAppStartedEvent event) {
        sink.beforeTestApp(event);
        watchers.forEach(watcher -> watcher.beforeTestApp(event));
    }

    private void sendAppFinishedEvent(TestAppFinishedEvent event) {
        sink.afterTestApp(event);
        watchers.forEach(watcher -> watcher.afterTestApp(event));
    }


    @Deprecated
    public FlightSnapshot getState() {
        RunnerState state = this.state;

        List<WorkerSnapshot> workerStates = pool.getAcquired().stream()
                .map(Worker::getSnapshot)
                .collect(Collectors.toList());

        Settings settings = state.getSettings();
        long remain = state.getTotalIterationsRemain();
        return new FlightSnapshot(state.getStage(), settings, state.getTestSuite(), state.getTestCase(),
                settings.getThreadIterationsCount() - remain, state.getTotalIterationsRemain(), state.getErrorCount(),
                Instant.EPOCH, Instant.EPOCH, state.getCaseElapsedTime(),
                state.getCaseRemainTime(), workerStates);
    }
}
