package ashes.of.bomber.runner;

import ashes.of.bomber.descriptions.ConfigurationDescription;
import ashes.of.bomber.configuration.Settings;
import ashes.of.bomber.descriptions.TestAppStateDescription;
import ashes.of.bomber.descriptions.TestAppDescription;
import ashes.of.bomber.descriptions.TestCaseDescription;
import ashes.of.bomber.descriptions.TestSuiteDescription;
import ashes.of.bomber.descriptions.WorkerDescription;
import ashes.of.bomber.events.TestAppFinishedEvent;
import ashes.of.bomber.events.TestAppStartedEvent;
import ashes.of.bomber.flight.TestAppPlan;
import ashes.of.bomber.flight.TestFlightReport;
import ashes.of.bomber.flight.TestFlightPlan;
import ashes.of.bomber.flight.TestCasePlan;
import ashes.of.bomber.flight.TestSuitePlan;
import ashes.of.bomber.flight.TestSuiteReport;
import ashes.of.bomber.sink.AsyncSink;
import ashes.of.bomber.sink.MultiSink;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.threads.BomberThreadFactory;
import ashes.of.bomber.watcher.Watcher;
import ashes.of.bomber.watcher.WatcherConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import javax.annotation.Nullable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class TestApp {
    private static final Logger log = LogManager.getLogger();

    private static final RunnerState IDLE = new RunnerState(() -> true);

    private final String name;
    private final WorkerPool pool;
    private final List<TestSuite<?>> testSuites;
    private final List<Sink> sinks;
    private final List<WatcherConfig> watchers;

    @Nullable
    private volatile TestFlightPlan plan;

    private volatile RunnerState state = IDLE;
    private volatile CountDownLatch endLatch = new CountDownLatch(1);

    public TestApp(String name, WorkerPool pool, List<TestSuite<?>> testSuites, List<Sink> sinks, List<WatcherConfig> watchers) {
        this.name = name;
        this.pool = pool;
        this.testSuites = testSuites;
        this.sinks = sinks;
        this.watchers = watchers;
    }

    public String getName() {
        return name;
    }

    public TestFlightPlan getFlightPlan() {
        return plan;
    }


    public void add(Sink sink) {
        sinks.add(sink);
    }

    public void add(long ms, Watcher watcher) {
        watchers.add(new WatcherConfig(ms, TimeUnit.MILLISECONDS, watcher));
    }

    void add(Duration duration, Watcher watcher) {
        add(duration.toMillis(), watcher);
    }


    /**
     * Start application with default flight plan which includes
     * all test suites with all test cases and default settings
     *
     * @return report
     */
    public TestFlightReport start() {
        var suites = getTestSuites().stream()
                .map(testSuite -> {
                    List<TestCasePlan> testCases = testSuite.getTestCases().stream()
                            .map(testCase -> new TestCasePlan(testCase.getName(), testCase.getConfiguration()))
                            .collect(Collectors.toList());

                    return new TestSuitePlan(testSuite.getName(), testCases);
                })
                .collect(Collectors.toList());

        var apps = new TestAppPlan(name, suites);

        var plan = new TestFlightPlan(System.currentTimeMillis() - 1630454400000L, List.of(apps));
        return start(plan);
    }

    public CompletableFuture<TestFlightReport> startAsync() {
        return CompletableFuture.supplyAsync(this::start);
    }

    /**
     * Start application with specified flight plan
     *
     * @param flightPlan flight plan with list of test suites, test cases and settings
     * @return report
     */
    public TestFlightReport start(TestFlightPlan flightPlan) {
        this.plan = flightPlan;
        ThreadContext.put("bomberApp", name);
        ThreadContext.put("flightId", String.valueOf(flightPlan.getFlightId()));

        log.info("Start application: {} flight: {}", name, flightPlan.getFlightId());
        var testAppPlan = flightPlan.getTestApps()
                .stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Flight plan doesn't contains this app: " + name));

        testAppPlan.getTestSuites()
                .forEach(testSuite -> {
                    log.debug("Planned test suite: {}", testSuite.getName());
                    testSuite.getTestCases().forEach(testCase -> {
                        log.debug("Planned test case: {}.{}, settings: {}", testSuite.getName(), testCase.getName(),
                                Optional.ofNullable(testCase.getConfiguration())
                                        .map(ConfigurationDescription::getSettings)
                                        .orElse(null));
                    });
                });

        ScheduledExecutorService watcherEx = Executors.newSingleThreadScheduledExecutor(BomberThreadFactory.watcher());

        List<ScheduledFuture<?>> wfs = watchers.stream()
                .map(config -> {
                    Watcher watcher = config.getWatcher();
                    return watcherEx.scheduleAtFixedRate(() -> watcher.watch(getDescription()), 0, config.getPeriod(), config.getTimeUnit());
                })
                .collect(Collectors.toList());

        Instant startTime = Instant.now();

        watchers.stream()
                .map(WatcherConfig::getWatcher)
                .forEach(Watcher::startUp);

        Sink sink = new AsyncSink(new MultiSink(sinks));

        sink.beforeTestApp(new TestAppStartedEvent(startTime, flightPlan.getFlightId(), name));

        List<TestSuiteReport> testSuiteReports = new ArrayList<>();
        try {
            log.debug("init runner");
            Runner runner = new Runner(name, pool, sink, testSuites);
            RunnerState state = new RunnerState(this::isStopped);
            this.state = state;
            testSuiteReports = runner.runTestApp(state, flightPlan.getFlightId(), testAppPlan);

            log.debug("All test suites finished, state -> Idle ");
            this.state = IDLE;
        } catch (Throwable th) {
            log.error("Unexpected throwable", th);
        }

        Instant finishTime = Instant.now();
        log.debug("Shutdown for each sink and watcher");
        sink.afterTestApp(new TestAppFinishedEvent(finishTime, flightPlan.getFlightId(), name));

        watchers.stream()
                .map(WatcherConfig::getWatcher)
                .forEach(Watcher::shutDown);

        log.debug("Shutdown wait each sink and watcher");
        wfs.forEach(wf -> wf.cancel(false));
        watcherEx.shutdown();

        endLatch.countDown();
        endLatch = new CountDownLatch(1);

        pool.shutdown();

        ThreadContext.clearAll();
        log.info("Flight is over, report is ready");
        return new TestFlightReport(flightPlan, startTime, finishTime, testSuiteReports);
    }

    public CompletableFuture<TestFlightReport> startAsync(TestFlightPlan plan) {
        return CompletableFuture.supplyAsync(() -> start(plan));
    }

    private boolean isStopped() {
        return endLatch.getCount() == 0;
    }


    public void await() throws InterruptedException {
        endLatch.await();
    }

    public void stop() {
        // todo check that application isn't idle
        log.info("stop application: {}", name);
        endLatch.countDown();
    }



    public TestAppDescription getDescription() {
        return new TestAppDescription(name, plan, getState(), getTestSuites());
    }


    public TestAppStateDescription getState() {
        RunnerState state = this.state;

        List<WorkerDescription> workerStates = pool.getAcquired().stream()
                .map(Worker::getDescription)
                .collect(Collectors.toList());

        Settings settings = state.getSettings();
        long remain = state.getTotalIterationsRemain();
        return new TestAppStateDescription(state.getStage(), settings, state.getTestSuite(), state.getTestCase(),
                settings.getThreadIterationsCount() - remain, state.getTotalIterationsRemain(), state.getErrorCount(),
                Instant.EPOCH, Instant.EPOCH, state.getCaseElapsedTime(),
                state.getCaseRemainTime(), workerStates);
    }

    public List<TestSuiteDescription> getTestSuites() {
        return testSuites.stream()
                .map(this::toTestSuite)
                .collect(Collectors.toList());
    }

    private TestSuiteDescription toTestSuite(TestSuite<?> suite) {
        List<TestCaseDescription> testCases = suite.getTestCases().stream()
                .map(this::toTestCase)
                .collect(Collectors.toList());

        return new TestSuiteDescription(suite.getName(), testCases);
    }

    private TestCaseDescription toTestCase(TestCase<?> testCase) {
        return new TestCaseDescription(testCase.getName(),
                new ConfigurationDescription(
                        testCase.getConfiguration().getWarmUp(),
                        testCase.getConfiguration().getSettings()
                )
        );
    }
}
