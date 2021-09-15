package ashes.of.bomber;

import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.configuration.Configuration;
import ashes.of.bomber.configuration.Settings;
import ashes.of.bomber.core.TestApp;
import ashes.of.bomber.snapshots.FlightSnapshot;
import ashes.of.bomber.snapshots.WorkerSnapshot;
import ashes.of.bomber.events.TestAppFinishedEvent;
import ashes.of.bomber.events.TestAppStartedEvent;
import ashes.of.bomber.flight.plan.TestAppPlan;
import ashes.of.bomber.flight.plan.TestFlightPlan;
import ashes.of.bomber.flight.report.TestAppReport;
import ashes.of.bomber.flight.report.TestFlightReport;
import ashes.of.bomber.flight.report.TestSuiteReport;
import ashes.of.bomber.runner.Runner;
import ashes.of.bomber.runner.RunnerState;
import ashes.of.bomber.runner.Worker;
import ashes.of.bomber.runner.WorkerPool;
import ashes.of.bomber.sink.AsyncSink;
import ashes.of.bomber.sink.MultiSink;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.threads.BomberThreadFactory;
import ashes.of.bomber.watcher.Watcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Bomber {
    private static final Logger log = LogManager.getLogger();

    private static final RunnerState IDLE = new RunnerState(() -> true);
    private volatile RunnerState state = IDLE;
    private volatile CountDownLatch endLatch = new CountDownLatch(1);
    private final WorkerPool pool = new WorkerPool();

    private final List<Sink> sinks;
    private final List<Watcher> watchers;
    private final List<TestApp> apps;

    public Bomber(List<Sink> sinks, List<Watcher> watchers, List<TestApp> apps) {
        this.sinks = sinks;
        this.watchers = watchers;
        this.apps = apps;
    }

    public Bomber addSink(Sink sink) {
        sinks.add(sink);
        return this;
    }

    public Bomber addWatcher(Watcher watcher) {
        watchers.add(watcher);
        return this;
    }

    public List<TestApp> getApps() {
        return apps;
    }

    public Bomber add(Class<?> app) {
        return add(TestAppBuilder.create(app));
    }

    public Bomber add(TestAppBuilder builder) {
        return add(builder.build());
    }

    public Bomber add(TestApp app) {
        apps.add(app);
        return this;
    }

    public TestFlightReport start(TestFlightPlan flightPlan) {
        log.info("start flight with plan: {}", flightPlan);
        Instant startTime = Instant.now();

        var appsByName = apps.stream()
                .collect(Collectors.toMap(TestApp::getName, app -> app));

        var reports = flightPlan.getTestApps().stream()
                .map(plan -> {

                    var testApp = appsByName.get(plan.getName());
                    if (testApp == null) {
                        log.warn("Bomber has no app with name: {}", plan.getName());
                        return null;
                    }

                    return startApp(testApp, flightPlan.getFlightId(), plan);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


        Instant finishTime = Instant.now();
        ThreadContext.clearAll();
        log.info("Flight is over, report is ready");
        return new TestFlightReport(flightPlan, startTime, finishTime, reports);
    }

    private TestAppReport startApp(TestApp app, long flightId, TestAppPlan plan) {
        ThreadContext.put("bomberApp", app.getName());
        ThreadContext.put("flightId", String.valueOf(flightId));

        // todo check that application isn't idle
        endLatch = new CountDownLatch(1);

        log.info("Start application: {} flight: {}", app.getName(), flightId);
        plan.getTestSuites()
                .forEach(testSuite -> {
                    log.debug("Planned test suite: {}", testSuite.getName());
                    testSuite.getTestCases().forEach(testCase -> {
                        log.debug("Planned test case: {}.{}, settings: {}", testSuite.getName(), testCase.getName(),
                                Optional.ofNullable(testCase.getConfiguration())
                                        .map(Configuration::getSettings)
                                        .orElse(null));
                    });
                });


        log.debug("Start async sink and watchers");
        ScheduledExecutorService watcherEx = Executors.newSingleThreadScheduledExecutor(BomberThreadFactory.watcher());

        List<ScheduledFuture<?>> wfs = watchers.stream()
                .map(watcher -> {
                    watcher.startUp();
                    return watcherEx.scheduleAtFixedRate(() -> watcher.watch(getState()), 0, 1, TimeUnit.SECONDS);
                })
                .collect(Collectors.toList());

        Instant startTime = Instant.now();

        Sink sink = new AsyncSink(new MultiSink(sinks));

        sink.beforeTestApp(new TestAppStartedEvent(startTime, flightId, app.getName()));

        List<TestSuiteReport> testSuiteReports = new ArrayList<>();
        log.debug("Start runner");
        try {
            Runner runner = new Runner(app.getName(), pool, sink, app.getTestSuites());
            RunnerState state = new RunnerState(() -> endLatch.getCount() == 0);
            this.state = state;
            testSuiteReports = runner.runTestApp(state, flightId, plan);

            log.debug("All test suites finished, state -> Idle ");
            this.state = IDLE;
        } catch (Throwable th) {
            log.error("Unexpected throwable", th);
        }

        Instant finishTime = Instant.now();
        log.debug("Shutdown for each sink and watcher");
        watchers.forEach(Watcher::shutDown);

        sink.afterTestApp(new TestAppFinishedEvent(finishTime, flightId, app.getName()));

        log.debug("Shutdown wait each sink and watcher");
        wfs.forEach(wf -> wf.cancel(false));
        watcherEx.shutdown();

        log.info("Application flight is over, report is ready");
        endLatch.countDown();
        endLatch = new CountDownLatch(1);
        pool.shutdown();

        ThreadContext.clearAll();
        return new TestAppReport(plan, app.getName(), startTime, finishTime, testSuiteReports);
    }

    public TestFlightReport start() {
        var apps = this.apps.stream()
                .map(TestApp::createDefaultAppPlan)
                .collect(Collectors.toList());

        var plan = new TestFlightPlan(System.currentTimeMillis() - 1630454400000L, apps);

        return start(plan);
    }


    public CompletableFuture<TestFlightReport> startAsync(TestFlightPlan plan) {
        return CompletableFuture.supplyAsync(() -> start(plan));
    }

    public void stop() {
        var latch = this.endLatch;
        if (latch != null) {
            latch.countDown();
        }
    }

    public void await() throws InterruptedException {
        endLatch.await();
    }


    @Deprecated
    public FlightSnapshot getState() {
        RunnerState state = this.state;

        List<WorkerSnapshot> workerStates = pool.getAcquired().stream()
                .map(Worker::getDescription)
                .collect(Collectors.toList());

        Settings settings = state.getSettings();
        long remain = state.getTotalIterationsRemain();
        return new FlightSnapshot(state.getStage(), settings, state.getTestSuite(), state.getTestCase(),
                settings.getThreadIterationsCount() - remain, state.getTotalIterationsRemain(), state.getErrorCount(),
                Instant.EPOCH, Instant.EPOCH, state.getCaseElapsedTime(),
                state.getCaseRemainTime(), workerStates);
    }
}
