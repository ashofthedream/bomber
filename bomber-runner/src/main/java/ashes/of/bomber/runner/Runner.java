package ashes.of.bomber.runner;

import ashes.of.bomber.configuration.Configuration;
import ashes.of.bomber.core.Test;
import ashes.of.bomber.core.TestApp;
import ashes.of.bomber.core.TestSuite;
import ashes.of.bomber.events.EventMachine;
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
    private final EventMachine em;
    private final List<Watcher> watchers;
    private final List<TestApp> apps;

    @Nullable
    private volatile TestFlightState state;

    public Runner(EventMachine em, List<Watcher> watchers, List<TestApp> apps) {
        this.em = em;
        this.watchers = watchers;
        this.apps = apps;
    }


    /**
     * Runs the test flight
     */
    public TestFlightReport run(TestFlightPlan flightPlan, BooleanSupplier condition) {
        var current = this.state;
        if (current != null) {
            throw new IllegalStateException("Invalid runner state: already exists for flightId: " + current.getPlan().flightId());
        }

        log.trace("Create flight state for flight: {}", flightPlan.flightId());
        var state = new TestFlightState(flightPlan, condition);
        this.state = state;


        ThreadContext.put("flightId", String.valueOf(flightPlan.flightId()));
        log.info("Start flight: {} with plan:", flightPlan.flightId());

        flightPlan.testApps().forEach(testApp -> {
            log.info("Test app: {}", testApp.name());
            testApp.testSuites().forEach(testSuite -> {
                log.info("    Test suite: {}", testSuite.name());
                testSuite.testCases().forEach(testCase -> {
                    var settings = Optional.ofNullable(testCase.configuration())
                            .map(Configuration::settings)
                            .orElse(null);

                    log.info("        Test case: {}, with: {}", testCase.name(), settings);
                });
            });
        });


        log.debug("Start watchers, watch every {}s", 1);
        ScheduledExecutorService watcherEx = Executors.newSingleThreadScheduledExecutor(BomberThreadFactory.watcher());
        List<ScheduledFuture<?>> wfs = watchers.stream()
                .map(watcher -> watcherEx.scheduleAtFixedRate(() -> watcher.watch(getFlight()), 0, 1, TimeUnit.SECONDS))
                .collect(Collectors.toList());


        em.dispatch(new FlightStartedEvent(state.getStartTime(), flightPlan.flightId()));

        try {
            var appsByName = apps.stream()
                    .collect(Collectors.toMap(TestApp::getName, app -> app));

            var reports = flightPlan.testApps().stream()
                    .map(plan -> {
                        var testApp = appsByName.get(plan.name());
                        if (testApp == null) {
                            log.warn("Bomber has no app with name: {}", plan.name());
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
            em.dispatch(new FlightFinishedEvent(flightFinishTime, flightPlan.flightId()));

            ThreadContext.clearAll();
            log.info("Flight finished: {}", flightPlan.flightId());
            return new TestFlightReport(flightPlan, state.getStartTime(), state.getFinishTime(), reports);
        } finally {
            log.debug("Stop watchers");
            wfs.forEach(wf -> wf.cancel(false));
            watcherEx.shutdown();

            pool.shutdown();

            ThreadContext.clearAll();
        }
    }


    /**
     * Runs the test app
     */
    public TestAppReport runTestApp(TestAppState state) {
        var testApp = state.getTestApp();
        ThreadContext.put("testApp", testApp.getName());
        log.info("Start app: {}", testApp.getName());

        em.dispatch(new TestAppStartedEvent(state.getStartTime(), state.getFlightId(), testApp.getName()));

        try {
            Map<String, TestSuite<?>> suitesByName = testApp.getTestSuitesByName();

            var testSuiteReports = state.getPlan().testSuites()
                    .stream()
                    .map(plan -> {
                        log.debug("Try to run test suite: {}", plan.name());
                        TestSuite<Object> testSuite = (TestSuite<Object>) suitesByName.get(plan.name());
                        if (testSuite == null) {
                            log.warn("Test suite: {} not found", plan.name());
                            return new TestSuiteReport(plan.name(), List.of());
                        }

                        var testSuiteState = new TestSuiteState(state, plan, testSuite);
                        state.attach(testSuiteState);
                        return runTestSuite(testSuiteState);
                    })
                    .collect(Collectors.toList());

            log.info("Finish application: {}", testApp.getName());
            state.finish();
            em.dispatch(new TestAppFinishedEvent(state.getFinishTime(), state.getFlightId(), testApp.getName()));

            return new TestAppReport(state.getPlan(), state.getStartTime(), state.getFinishTime(), testSuiteReports);
        } finally {
            ThreadContext.remove("testApp");
        }
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

        em.dispatch(new TestSuiteStartedEvent(state.getStartTime(), state.getFlightId(), state.getParent().getTestApp().getName(), testSuite.getName()));

        int threads = determineWorkerThreadsCount(state.getPlan(), testSuite);
        pool.acquire(threads);

        try {
            callBeforeSuite(testSuite);

            List<TestCaseReport> reports = state.getPlan().testCases()
                    .stream()
                    .map(plan -> {
                        var testCase = testSuite.getTestCase(plan.name());
                        if (testCase == null) {
                            log.warn("Test case: {} not found in test suite: {}, but it exists in the plan",
                                    plan.name(), testSuite.getName());
                            return null;
                        }

                        // merge configuration
                        var initial = testCase.getConfiguration();
                        Configuration configuration = Optional.ofNullable(plan.configuration())
                                .map(actual -> new Configuration(
                                        // todo get these properties from actual
                                        initial.delayer(),
                                        initial.limiter(),
                                        initial.barrier(),
                                        actual.settings()
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
            em.dispatch(new TestSuiteFinishedEvent(state.getFinishTime(), state.getFlightId(), state.getParent().getTestApp().getName(), testSuite.getName()));

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
        return testSuitePlan.testCases().stream()
                .mapToInt(testCasePlan -> {
                    var testCase = testSuite.getTestCase(testCasePlan.name());
                    if (testCase == null) {
                        log.trace("No test case: {} found, return 0", testCasePlan.name());
                        return 0;
                    }

                    var config = Optional.ofNullable(testCasePlan.configuration())
                            .orElse(testCase.getConfiguration());

                    return config.settings().threadsCount();
                })
                .max()
                .orElseThrow(() -> new RuntimeException("Can't determine thread count for test suite: " + testSuite.getName()));
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
        var settings = state.getConfiguration().settings();

        ThreadContext.put("testCase", testCase.getName());
        log.debug("Start test case: {}", testCase.getName());

        log.trace("Reset beforeCase & afterCase lifecycle methods");
        testSuite.resetBeforeAndAfterCase();

        var test = new Test(testApp.getName(), testSuite.getName(), testCase.getName());
        em.dispatch(new TestCaseStartedEvent(state.getStartTime(), state.getFlightId(), test, settings));

        log.debug("Run {} workers", settings.threadsCount());

        pool.getAcquired()
                .stream()
                .limit(settings.threadsCount())
                .forEach(worker -> worker.run(state, em));

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

        em.dispatch(new TestCaseFinishedEvent(state.getFinishTime(), state.getFlightId(), test));

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
                state.getConfiguration().settings(),
                state.getStartTime(),
                state.getFinishTime(),
                state.getTotalIterationsCount(),
                state.getErrorCount()
        );
    }
}
