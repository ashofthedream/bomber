package ashes.of.bomber.runner;

import ashes.of.bomber.core.BomberApp;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.StateModel;
import ashes.of.bomber.core.TestCaseModel;
import ashes.of.bomber.core.TestSuiteModel;
import ashes.of.bomber.core.WorkerStateModel;
import ashes.of.bomber.flight.FlightReport;
import ashes.of.bomber.flight.FlightPlan;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.watcher.Watcher;
import ashes.of.bomber.watcher.WatcherConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class TestApp implements BomberApp {
    private static final Logger log = LogManager.getLogger();

    private static final RunnerState IDLE = new RunnerState(() -> true);

    private final String name;
    private final WorkerPool pool;
    private final Environment env;
    private final List<TestSuite<?>> testSuites;

    private volatile FlightPlan plan;

    private volatile RunnerState state = IDLE;
    private volatile CountDownLatch endLatch = new CountDownLatch(1);

    public TestApp(String name, WorkerPool pool, Environment env, List<TestSuite<?>> testSuites) {
        this.name = name;
        this.pool = pool;
        this.env = env;
        this.testSuites = testSuites;
    }

    @Override
    public FlightPlan getFlightPlan() {
        return plan;
    }

    @Override
    public void add(Sink sink) {
        env.getSinks().add(sink);
    }

    @Override
    public void add(long ms, Watcher watcher) {
        env.getWatchers().add(new WatcherConfig(ms, TimeUnit.MILLISECONDS, watcher));
    }

    @Override
    public StateModel getState() {
        RunnerState state = this.state;

        List<WorkerStateModel> workerStates = pool.getAcquired().stream()
                .map(worker -> {
                    WorkerState ws = worker.getState();
                    return new WorkerStateModel(worker.getName(),
                            ws.getCurrentIterationsCount(), ws.getRemainIterationsCount(), ws.getErrorsCount(),
                            ws.getExpectedRecordsCount(), ws.getCaughtRecordsCount());
                })
                .collect(Collectors.toList());

        Settings settings = state.getSettings();
        long remain = state.getTotalIterationsRemain();
        return new StateModel(state.getStage(), settings, state.getTestSuite(), state.getTestCase(),
                settings.getThreadIterationsCount() - remain, state.getTotalIterationsRemain(), state.getErrorCount(),
                Instant.EPOCH, Instant.EPOCH, state.getCaseElapsedTime(),
                state.getCaseRemainTime(), workerStates);
    }

    @Override
    public FlightReport start(FlightPlan plan) {
        this.plan = plan;
        ThreadContext.put("bomberApp", name);
        ThreadContext.put("flightId", String.valueOf(plan.getId()));

        List<String> testSuiteNames = plan.getTestSuites().stream()
                .map(testSuite -> String.format("%s %s", testSuite.getName(), testSuite.getTestCases()))
                .collect(Collectors.toList());

        log.info("start flight: {} of test app with {} suites: {}", plan.getId(), plan.getTestSuites().size(), testSuiteNames);

        ScheduledExecutorService watcherEx = Executors.newSingleThreadScheduledExecutor();

        List<ScheduledFuture<?>> wfs = env.getWatchers()
                .stream()
                .map(config -> {
                    Watcher watcher = config.getWatcher();
                    return watcherEx.scheduleAtFixedRate(() -> watcher.watch(this), 0, config.getPeriod(), config.getTimeUnit());
                })
                .collect(Collectors.toList());

        Instant startTime = Instant.now();

        env.getWatchers().stream()
                .map(WatcherConfig::getWatcher)
                .forEach(Watcher::startUp);

        env.getSinks()
                .forEach(Sink::startUp);


        try {
            log.debug("init runner");
            Runner runner = new Runner(pool, env.getSinks());

            Map<String, TestSuite<?>> suitesByName = testSuites.stream()
                    .collect(Collectors.toMap(TestSuite::getName, suite -> suite));

            plan.getTestSuites()
                    .forEach(planned -> {
                        log.debug("try to run testSuite: {}", planned.getName());
                        TestSuite<Object> testSuite = (TestSuite<Object>) suitesByName.get(planned.getName());

                        RunnerState state = new RunnerState(this::isStop);
                        this.state = state;
                        runner.startTestCase(testSuite.getEnv(), state, testSuite, planned.getTestCases());
                    });

            log.debug("All test suites finished, state -> Idle ");
            state = IDLE;
        } catch (Throwable th) {
            log.error("Unexpected throwable", th);
        }

        log.debug("Shutdown for each sink and watcher");
        env.getSinks()
                .forEach(Sink::shutDown);

        env.getWatchers().stream()
                .map(WatcherConfig::getWatcher)
                .forEach(Watcher::shutDown);

        log.debug("Shutdown wat each sink and watcher");
        watcherEx.shutdown();

        Instant finishTime = Instant.now();
        endLatch.countDown();
        endLatch = new CountDownLatch(1);

        pool.shutdown();

        ThreadContext.clearAll();
        log.info("Flight is over, report is ready");
        return new FlightReport(plan, startTime, finishTime);
    }

    @Override
    public void await() throws InterruptedException {
        endLatch.await();
    }

    @Override
    public void stop() {
        log.info("stop");
        endLatch.countDown();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<TestSuiteModel> getTestSuites() {
        return testSuites.stream()
                .map(this::toTestSuite)
                .collect(Collectors.toList());
    }

    private TestSuiteModel toTestSuite(TestSuite<?> suite) {
        List<TestCaseModel> testCases = suite.getTestCases().stream()
                .map(testCase -> new TestCaseModel(testCase.getName(), testCase.isAsync()))
                .collect(Collectors.toList());

        return new TestSuiteModel(suite.getName(), testCases, suite.getSettings(), suite.getWarmUp());
    }

    public boolean isStop() {
        return endLatch.getCount() == 0;
    }
}
