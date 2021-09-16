package ashes.of.bomber.runner;

import ashes.of.bomber.configuration.Configuration;
import ashes.of.bomber.configuration.Settings;
import ashes.of.bomber.core.TestApp;
import ashes.of.bomber.core.TestCase;
import ashes.of.bomber.core.TestSuite;
import ashes.of.bomber.events.FlightFinishedEvent;
import ashes.of.bomber.events.FlightStartedEvent;
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
import ashes.of.bomber.threads.BomberThreadFactory;
import ashes.of.bomber.watcher.Watcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.time.Instant;
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


    /**
     * Runs the test flight
     */
    public TestFlightReport run(TestFlightPlan flightPlan) {
        ThreadContext.put("flightId", String.valueOf(flightPlan.getFlightId()));
        log.info("Start flight: {}", flightPlan.getFlightId());

        flightPlan.getTestApps().forEach(testAppPlan -> {
            log.debug("App: {}", testAppPlan.getName());
            testAppPlan.getTestSuites().forEach(testSuite -> {
                log.debug("    Test suite: {}", testSuite.getName());
                testSuite.getTestCases().forEach(testCase -> {
                    var settings = Optional.ofNullable(testCase.getConfiguration())
                            .map(Configuration::getSettings)
                            .orElse(null);

                    log.debug("        Test case: {}, with: {}", testCase.getName(), settings);
                });
            });
        });

        log.debug("Start watchers, watch every {}s", 1);
        ScheduledExecutorService watcherEx = Executors.newSingleThreadScheduledExecutor(BomberThreadFactory.watcher());
        List<ScheduledFuture<?>> wfs = watchers.stream()
                .map(watcher -> watcherEx.scheduleAtFixedRate(() -> watcher.watch(getState()), 0, 1, TimeUnit.SECONDS))
                .collect(Collectors.toList());


        Instant flightStartTime = Instant.now();
        sendFlightStartedEvent(new FlightStartedEvent(flightStartTime, flightPlan.getFlightId()));

        try {
            var appsByName = apps.stream()
                    .collect(Collectors.toMap(TestApp::getName, app -> app));

            var reports = flightPlan.getTestApps().stream()
                    .map(testAppPlan -> {
                        var testApp = appsByName.get(testAppPlan.getName());
                        if (testApp == null) {
                            log.warn("Bomber has no app with name: {}", testAppPlan.getName());
                            return new TestAppReport(testAppPlan, testAppPlan.getName(), Instant.now(), Instant.now(), List.of());
                        }

                        try {
                            return runTestApp(flightPlan.getFlightId(), testAppPlan, testApp);
                        } catch (Throwable th) {
                            log.error("Unexpected throwable", th);
                            throw th;
                        }

                        // todo return report with error?
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());


            Instant flightFinishTime = Instant.now();
            sendFlightFinishedEvent(new FlightFinishedEvent(flightFinishTime, flightPlan.getFlightId()));

            ThreadContext.clearAll();
            log.info("Flight finished: {}", flightPlan.getFlightId());
            return new TestFlightReport(flightPlan, flightStartTime, flightFinishTime, reports);
        } finally {
            log.debug("Stop watchers");
            wfs.forEach(wf -> wf.cancel(false));
            watcherEx.shutdown();

            pool.shutdown();

            ThreadContext.clearAll();
        }
    }

    private void sendFlightStartedEvent(FlightStartedEvent event) {
        sink.beforeFlight(event);
        watchers.forEach(watcher -> watcher.beforeFlight(event));
    }

    private void sendFlightFinishedEvent(FlightFinishedEvent event) {
        sink.afterFlight(event);
        watchers.forEach(watcher -> watcher.afterFlight(event));
    }


    /**
     * Runs the test app
     */
    public TestAppReport runTestApp(long flightId, TestAppPlan testAppPlan, TestApp testApp) {
        ThreadContext.put("testApp", testApp.getName());

        Instant startTime = Instant.now();
        sendAppStartedEvent(new TestAppStartedEvent(startTime, flightId, testApp.getName()));

        try {
            Map<String, TestSuite<?>> suitesByName = testApp.getTestSuitesByName();

            var testSuiteReports = testAppPlan.getTestSuites()
                    .stream()
                    .map(testSuitePlan -> {
                        log.debug("Try to run test suite: {}", testSuitePlan.getName());
                        TestSuite<Object> testSuite = (TestSuite<Object>) suitesByName.get(testSuitePlan.getName());
                        if (testSuite == null) {
                            log.warn("Test suite: {} not found", testSuitePlan.getName());
                            return new TestSuiteReport(testSuitePlan.getName(), List.of());
                        }

                        return runTestSuite(flightId, testSuitePlan, testApp, testSuite);
                    })
                    .collect(Collectors.toList());

            log.info("Finish application: {} flight: {}", testApp.getName(), flightId);
            Instant finishTime = Instant.now();

            sendAppFinishedEvent(new TestAppFinishedEvent(finishTime, flightId, testApp.getName()));

            return new TestAppReport(testAppPlan, testApp.getName(), startTime, finishTime, testSuiteReports);
        } finally {
            ThreadContext.remove("testApp");
        }
    }

    private void sendAppStartedEvent(TestAppStartedEvent event) {
        sink.beforeTestApp(event);
        watchers.forEach(watcher -> watcher.beforeTestApp(event));
    }

    private void sendAppFinishedEvent(TestAppFinishedEvent event) {
        sink.afterTestApp(event);
        watchers.forEach(watcher -> watcher.afterTestApp(event));
    }


    /**
     * Runs the test case
     */
    private TestSuiteReport runTestSuite(long flightId, TestSuitePlan plan, TestApp testApp, TestSuite<Object> testSuite) {
        ThreadContext.put("testSuite", testSuite.getName());
        log.info("Run test suite: {}", testSuite.getName());

        log.trace("Reset before & after test suite lifecycle methods");
        testSuite.resetBeforeAndAfterSuite();
        var startTime = Instant.now();
        state.startSuiteIfNotStarted(testSuite.getName());

        sendSuiteStartedEvent(new TestSuiteStartedEvent(startTime, flightId, testApp.getName(), testSuite.getName()));

        int threads = determineWorkerThreadsCount(plan, testSuite);
        pool.acquire(threads);

        try {
            awaitBeforeSuite(testSuite);

            List<TestCaseReport> reports = plan.getTestCases()
                    .stream()
                    .map(testCasePlan -> {
                        var testCase = testSuite.getTestCase(testCasePlan.getName());
                        if (testCase == null) {
                            log.warn("Test case: {} not found in test suite: {}, but it exists in the plan",
                                    testCasePlan.getName(), testSuite.getName());
                            return null;
                        }

                        return runTestCase(flightId, testApp, testSuite, testCase, testCasePlan);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            awaitAfterSuite(testSuite);

            sendSuiteFinishedEvent(new TestSuiteFinishedEvent(Instant.now(), flightId, testApp.getName(), testSuite.getName()));

            return new TestSuiteReport(testSuite.getName(), reports);
        } catch (Throwable throwable) {
            log.error("Run test suite: {} failed", testSuite.getName(), throwable);
            return new TestSuiteReport(testSuite.getName(), List.of());
        } finally {
            pool.releaseAll();
            state.finishSuite();
            ThreadContext.remove("testSuite");
        }
    }

    private int determineWorkerThreadsCount(TestSuitePlan testSuitePlan, TestSuite<Object> testSuite) {
        log.debug("Determine worker threads count for test suite: {}", testSuite.getName());
        return testSuitePlan.getTestCases().stream()
                .mapToInt(testCasePlan -> {
                    var testCase = testSuite.getTestCase(testCasePlan.getName());
                    if (testCase == null) {
                        log.trace("No test case: {} found, return 0", testCasePlan.getName());
                        return 0;
                    }

                    var configuration = Optional.ofNullable(testCasePlan.getConfiguration())
                            .orElse(testCase.getConfiguration());

                    return configuration.getSettings().getThreadsCount();
                })
                .max()
                .orElseThrow(() -> new RuntimeException("Can't determine thread count for test suite: " + testSuite.getName()));
    }

    private void sendSuiteStartedEvent(TestSuiteStartedEvent event) {
        sink.beforeTestSuite(event);
        watchers.forEach(watcher -> watcher.beforeTestSuite(event));
    }

    private void sendSuiteFinishedEvent(TestSuiteFinishedEvent event) {
        sink.afterTestSuite(event);
        watchers.forEach(watcher -> watcher.afterTestSuite(event));
    }

    private void awaitBeforeSuite(TestSuite<Object> testSuite) throws InterruptedException {
        var acquired = pool.getAcquired();
        log.debug("Call beforeSuite for all workers: {}", acquired.size());
        CountDownLatch latch = new CountDownLatch(acquired.size());
        acquired.forEach(worker -> worker.runBeforeSuite(testSuite, latch));

        log.debug("Await workers beforeSuite");
        latch.await();
    }

    private void awaitAfterSuite(TestSuite<Object> testSuite) throws InterruptedException {
        var acquired = pool.getAcquired();
        log.debug("Call afterSuite for all workers: {}", acquired.size());
        CountDownLatch latch = new CountDownLatch(acquired.size());
        acquired.forEach(worker -> worker.runAfterSuite(testSuite, latch));

        log.debug("Await workers afterSuite");
        latch.await();
    }

    private TestCaseReport runTestCase(long flightId, TestApp testApp, TestSuite<Object> testSuite, TestCase<Object> testCase, TestCasePlan testCasePlan) {
        ThreadContext.put("testCase", testCase.getName());
        log.debug("Run TestCase: {}", testCase.getName());

        var config = Optional.ofNullable(testCasePlan.getConfiguration());

        Settings settings = config.map(Configuration::getSettings)
                .orElse(testCase.getConfiguration()
                        .getSettings());

        log.trace("Reset before & after test case lifecycle methods");
        testSuite.resetBeforeAndAfterCase();

        state.startCaseIfNotStarted(testCase.getName(), settings);

        CountDownLatch startLatch = new CountDownLatch(settings.getThreadsCount());
        CountDownLatch finishLatch = new CountDownLatch(settings.getThreadsCount());

        log.debug("Run {} workers", settings.getThreadsCount());

        pool.getAcquired()
                .stream()
                .limit(settings.getThreadsCount())
                .forEach(worker -> worker.runTestCase(state, flightId, testApp, testSuite, testCase, settings, startLatch, finishLatch, sink));

        try {
            log.debug("Await end of test case: {}", testCase.getName());
            finishLatch.await();

            log.debug("All workers done, 1s cooldown");
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            log.error("We've been interrupted", e);
        }

        log.info("Finish test case: {}, elapsed time: {}ms", testCase.getName(), state.getCaseElapsedTime());
        state.finishCase();

        ThreadContext.remove("testCase");
        return new TestCaseReport(
                testCase.getName(),
                settings,
                settings.getTotalIterationsCount() - state.getTotalIterationsRemain(),
                state.getErrorCount(),
                state.getCaseElapsedTime()
        );
    }


    @Deprecated
    public FlightSnapshot getState() {
        RunnerState state = this.state;

        List<WorkerSnapshot> workerStates = pool.getAcquired().stream()
                .map(Worker::getSnapshot)
                .collect(Collectors.toList());

        Settings settings = state.getSettings();
        long remain = state.getTotalIterationsRemain();
        return new FlightSnapshot(settings, state.getTestApp(), state.getTestSuite(), state.getTestCase(),
                settings.getThreadIterationsCount() - remain, state.getTotalIterationsRemain(), state.getErrorCount(),
                Instant.EPOCH, Instant.EPOCH, state.getCaseElapsedTime(),
                state.getCaseRemainTime(), workerStates);
    }
}
