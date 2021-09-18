package ashes.of.bomber.runner;

import ashes.of.bomber.configuration.Configuration;
import ashes.of.bomber.core.TestApp;
import ashes.of.bomber.core.TestSuite;
import ashes.of.bomber.events.FlightFinishedEvent;
import ashes.of.bomber.events.FlightStartedEvent;
import ashes.of.bomber.events.TestAppFinishedEvent;
import ashes.of.bomber.events.TestAppStartedEvent;
import ashes.of.bomber.events.TestCaseFinishedEvent;
import ashes.of.bomber.events.TestCaseStartedEvent;
import ashes.of.bomber.events.TestSuiteFinishedEvent;
import ashes.of.bomber.events.TestSuiteStartedEvent;
import ashes.of.bomber.flight.plan.TestFlightPlan;
import ashes.of.bomber.flight.plan.TestSuitePlan;
import ashes.of.bomber.flight.report.TestAppReport;
import ashes.of.bomber.flight.report.TestCaseReport;
import ashes.of.bomber.flight.report.TestFlightReport;
import ashes.of.bomber.flight.report.TestSuiteReport;
import ashes.of.bomber.sink.AsyncSink;
import ashes.of.bomber.sink.MultiSink;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.snapshots.TestAppSnapshot;
import ashes.of.bomber.snapshots.TestCaseSnapshot;
import ashes.of.bomber.snapshots.TestFlightSnapshot;
import ashes.of.bomber.snapshots.TestSuiteSnapshot;
import ashes.of.bomber.snapshots.WorkerSnapshot;
import ashes.of.bomber.threads.BomberThreadFactory;
import ashes.of.bomber.watcher.Watcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import javax.annotation.Nullable;
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
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;


public class Runner {
    private static final Logger log = LogManager.getLogger();

    private final WorkerPool pool = new WorkerPool();
    private final List<Sink> sinks;
    private final Sink sink;
    private final List<Watcher> watchers;
    private final List<TestApp> apps;

    @Nullable
    private volatile TestFlightState state;

    public Runner(List<Sink> sinks, List<Watcher> watchers, List<TestApp> apps) {
        this.sinks = sinks;
        this.sink = new AsyncSink(new MultiSink(sinks));
        this.watchers = watchers;
        this.apps = apps;
    }


    /**
     * Runs the test flight
     */
    public TestFlightReport run(TestFlightPlan flightPlan, BooleanSupplier condition) {
        var current = this.state;
        if (current != null) {
            throw new IllegalStateException("Invalid runner state: already exists for flightId: " + current.getPlan().getFlightId());
        }

        log.trace("Create flight state for flight: {}", flightPlan.getFlightId());
        var state = new TestFlightState(flightPlan, condition);
        this.state = state;


        ThreadContext.put("flightId", String.valueOf(flightPlan.getFlightId()));
        log.info("Start flight: {} with plan:", flightPlan.getFlightId());

        flightPlan.getTestApps().forEach(testApp -> {
            log.info("Test app: {}", testApp.getName());
            testApp.getTestSuites().forEach(testSuite -> {
                log.info("    Test suite: {}", testSuite.getName());
                testSuite.getTestCases().forEach(testCase -> {
                    var settings = Optional.ofNullable(testCase.getConfiguration())
                            .map(Configuration::getSettings)
                            .orElse(null);

                    log.info("        Test case: {}, with: {}", testCase.getName(), settings);
                });
            });
        });


        log.debug("Start watchers, watch every {}s", 1);
        ScheduledExecutorService watcherEx = Executors.newSingleThreadScheduledExecutor(BomberThreadFactory.watcher());
        List<ScheduledFuture<?>> wfs = watchers.stream()
                .map(watcher -> watcherEx.scheduleAtFixedRate(() -> watcher.watch(getFlight()), 0, 1, TimeUnit.SECONDS))
                .collect(Collectors.toList());

        
        sendFlightStartedEvent(new FlightStartedEvent(state.getStartTime(), flightPlan.getFlightId()));

        try {
            var appsByName = apps.stream()
                    .collect(Collectors.toMap(TestApp::getName, app -> app));

            var reports = flightPlan.getTestApps().stream()
                    .map(plan -> {
                        var testApp = appsByName.get(plan.getName());
                        if (testApp == null) {
                            log.warn("Bomber has no app with name: {}", plan.getName());
                            return new TestAppReport(plan, Instant.now(), Instant.now(), List.of());
                        }

                        // todo return report with error? but now fail fast approach used

                        var testAppState = new TestAppState(state, plan, testApp);
                        state.attach(testAppState);

                        return runTestApp(testAppState);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());


            Instant flightFinishTime = Instant.now();
            state.setFinishTime(flightFinishTime);
            sendFlightFinishedEvent(new FlightFinishedEvent(flightFinishTime, flightPlan.getFlightId()));

            ThreadContext.clearAll();
            log.info("Flight finished: {}", flightPlan.getFlightId());
            return new TestFlightReport(flightPlan, state.getStartTime(), state.getFinishTime(), reports);
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
    public TestAppReport runTestApp(TestAppState state) {
        var testApp = state.getTestApp();
        ThreadContext.put("testApp", testApp.getName());
        log.info("Start app: {}", testApp.getName());

        send(new TestAppStartedEvent(state.getStartTime(), state.getFlightId(), testApp.getName()));

        try {
            Map<String, TestSuite<?>> suitesByName = testApp.getTestSuitesByName();

            var testSuiteReports = state.getPlan().getTestSuites()
                    .stream()
                    .map(plan -> {
                        log.debug("Try to run test suite: {}", plan.getName());
                        TestSuite<Object> testSuite = (TestSuite<Object>) suitesByName.get(plan.getName());
                        if (testSuite == null) {
                            log.warn("Test suite: {} not found", plan.getName());
                            return new TestSuiteReport(plan.getName(), List.of());
                        }

                        var testSuiteState = new TestSuiteState(state, plan, testSuite);
                        state.attach(testSuiteState);
                        return runTestSuite(testSuiteState);
                    })
                    .collect(Collectors.toList());

            log.info("Finish application: {}", testApp.getName());
            state.finish();
            send(new TestAppFinishedEvent(state.getFinishTime(), state.getFlightId(), testApp.getName()));

            return new TestAppReport(state.getPlan(), state.getStartTime(), state.getFinishTime(), testSuiteReports);
        } finally {
            ThreadContext.remove("testApp");
        }
    }

    private void send(TestAppStartedEvent event) {
        sink.beforeTestApp(event);
        watchers.forEach(watcher -> watcher.beforeTestApp(event));
    }

    private void send(TestAppFinishedEvent event) {
        sink.afterTestApp(event);
        watchers.forEach(watcher -> watcher.afterTestApp(event));
    }


    /**
     * Runs the test suite
     */
    private TestSuiteReport runTestSuite(TestSuiteState state) {
        var testSuite = state.getTestSuite();
        ThreadContext.put("testSuite", testSuite.getName());
        log.info("Start test suite: {}", testSuite.getName());

        log.trace("Reset before & after test suite lifecycle methods");
        testSuite.resetBeforeAndAfterSuite();

        send(new TestSuiteStartedEvent(state.getStartTime(), state.getFlightId(), state.getParent().getTestApp().getName(), testSuite.getName()));

        int threads = determineWorkerThreadsCount(state.getPlan(), testSuite);
        pool.acquire(threads);

        try {
            callBeforeSuite(testSuite);

            List<TestCaseReport> reports = state.getPlan().getTestCases()
                    .stream()
                    .map(plan -> {
                        var testCase = testSuite.getTestCase(plan.getName());
                        if (testCase == null) {
                            log.warn("Test case: {} not found in test suite: {}, but it exists in the plan",
                                    plan.getName(), testSuite.getName());
                            return null;
                        }

                        // merge configuration
                        var initial = testCase.getConfiguration();
                        Configuration configuration = Optional.ofNullable(plan.getConfiguration())
                                .map(actual -> new Configuration(
                                        // todo get these properties from actual
                                        initial.getDelayer(),
                                        initial.getLimiter(),
                                        initial.getBarrier(),
                                        actual.getSettings()
                                ))
                                .orElse(initial);

                        var testCaseState = new TestCaseState(state, plan, testCase, configuration);
                        state.attach(testCaseState);
                        return runTestCase(testCaseState);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            callAfterSuite(testSuite);

            log.info("Finish test suite: {}", testSuite.getName());
            state.finish();
            send(new TestSuiteFinishedEvent(state.getFinishTime(), state.getFlightId(), state.getParent().getTestApp().getName(), testSuite.getName()));

            return new TestSuiteReport(testSuite.getName(), reports);
        } catch (Throwable throwable) {
            log.error("Run test suite: {} failed", testSuite.getName(), throwable);
            return new TestSuiteReport(testSuite.getName(), List.of());
        } finally {
            pool.releaseAll();
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

    private void send(TestSuiteStartedEvent event) {
        sink.beforeTestSuite(event);
        watchers.forEach(watcher -> watcher.beforeTestSuite(event));
    }

    private void send(TestSuiteFinishedEvent event) {
        sink.afterTestSuite(event);
        watchers.forEach(watcher -> watcher.afterTestSuite(event));
    }

    private void callBeforeSuite(TestSuite<Object> testSuite) throws InterruptedException {
        var acquired = pool.getAcquired();
        log.debug("Call beforeSuite for all workers: {}", acquired.size());
        CountDownLatch latch = new CountDownLatch(acquired.size());
        acquired.forEach(worker -> worker.runBeforeSuite(testSuite, latch));

        log.debug("Await {} workers beforeSuite", acquired.size());
        latch.await();
    }

    private void callAfterSuite(TestSuite<Object> testSuite) throws InterruptedException {
        var acquired = pool.getAcquired();
        log.debug("Call afterSuite for all workers: {}", acquired.size());
        CountDownLatch latch = new CountDownLatch(acquired.size());
        acquired.forEach(worker -> worker.runAfterSuite(testSuite, latch));

        log.debug("Await {} workers afterSuite", acquired.size());
        latch.await();
    }


    /**
     * Runs the test case
     */
    private TestCaseReport runTestCase(TestCaseState state) {
        var testCase = state.getTestCase();
        var testSuite = state.getTestSuite();
        var testApp = state.getTestApp();
        var settings = state.getConfiguration().getSettings();

        ThreadContext.put("testCase", testCase.getName());
        log.debug("Start test case: {}", testCase.getName());

        log.trace("Reset beforeCase & afterCase lifecycle methods");
        testSuite.resetBeforeAndAfterCase();


        send(new TestCaseStartedEvent(state.getStartTime(), state.getFlightId(), testApp.getName(), testSuite.getName(), testCase.getName(), settings));

        log.debug("Run {} workers", settings.getThreadsCount());

        pool.getAcquired()
                .stream()
                .limit(settings.getThreadsCount())
                .forEach(worker -> worker.run(state, sink, watchers));

        try {
            log.debug("Await end of test case: {}", testCase.getName());
            state.awaitFinish();

            log.debug("All workers done, 1s cooldown");
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            log.error("We've been interrupted", e);
        }


        log.info("Finish test case: {}", testCase.getName());
        state.finish();

        send(new TestCaseFinishedEvent(state.getFinishTime(), state.getFlightId(), testApp.getName(), testSuite.getName(), testCase.getName()));

        var elapsed = state.getFinishTime().toEpochMilli() - state.getStartTime().toEpochMilli();
        ThreadContext.remove("testCase");
        return new TestCaseReport(
                testCase.getName(),
                settings,
                state.getTotalIterationsCount(),
                state.getErrorCount(),
                elapsed
        );
    }

    private void send(TestCaseStartedEvent event) {
        sink.beforeTestCase(event);
        watchers.forEach(watcher -> watcher.beforeTestCase(event));
    }

    private void send(TestCaseFinishedEvent event) {
        sink.afterTestCase(event);
        watchers.forEach(watcher -> watcher.afterTestCase(event));
    }


    @Deprecated
    @Nullable
    public TestFlightSnapshot getFlight() {
        var state = this.state;
        if (state == null) {
            return null;
        }

        List<WorkerSnapshot> workers = pool.getAcquired().stream()
                .map(Worker::getSnapshot)
                .collect(Collectors.toList());

        return new TestFlightSnapshot(
                state.getPlan(),
                toSnapshot(state.getCurrent()),
                workers
        );
    }

    private TestAppSnapshot toSnapshot(TestAppState state) {
        if (state == null) {
            return null;
        }

        return new TestAppSnapshot(
                state.getTestApp().getName(),
                toSnapshot(state.getCurrent())
        );
    }

    private TestSuiteSnapshot toSnapshot(TestSuiteState state) {
        if (state == null) {
            return null;
        }

        return new TestSuiteSnapshot(
                state.getTestSuite().getName(),
                toSnapshot(state.getCurrent())
        );
    }

    private TestCaseSnapshot toSnapshot(TestCaseState state) {
        if (state == null) {
            return null;
        }

        return new TestCaseSnapshot(
                state.getTestCase().getName(),
                state.getConfiguration().getSettings(),
                state.getStartTime(),
                state.getFinishTime(),
                state.getTotalIterationsCount(),
                state.getErrorCount()
        );
    }
}
