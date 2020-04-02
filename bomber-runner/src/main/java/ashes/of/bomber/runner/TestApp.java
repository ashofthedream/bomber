package ashes.of.bomber.runner;

import ashes.of.bomber.core.*;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.watcher.Watcher;
import ashes.of.bomber.watcher.WatcherConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class TestApp implements BomberApp {
    private static final Logger log = LogManager.getLogger();

    private static final State REST = new State(Stage.Idle, new Settings().disabled(), "", () -> false);

    private final String name;
    private final WorkerPool pool;
    private final Environment env;
    private final List<TestSuite<?>> suites;

    private volatile long flightId;

    @Nullable
    private volatile State state;
    private volatile CountDownLatch endLatch = new CountDownLatch(1);

    public TestApp(String name, WorkerPool pool, Environment env, List<TestSuite<?>> suites) {
        this.name = name;
        this.pool = pool;
        this.env = env;
        this.suites = suites;
    }

    @Override
    public long getFlightId() {
        return flightId;
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
        State state = Optional.ofNullable(this.state)
                .orElse(REST);

        List<WorkerStateModel> workerStates = pool.getAcquired().stream()
                .map(worker -> {
                    WorkerState ws = worker.getState();
                    return new WorkerStateModel(worker.getName(), ws.currentItNumber(), ws.getRemainIterationsCount(), ws.getErrorsCount());
                })
                .collect(Collectors.toList());

        Settings settings = state.getSettings();
        long remain = state.getTotalIterationsRemain();
        return new StateModel(state.getStage(), settings, state.getTestSuite(), state.getTestCase(),
                settings.getThreadIterationsCount() - remain, state.getTotalIterationsRemain(), state.getErrorCount(),
                Instant.EPOCH, Instant.EPOCH, state.getCaseElapsedTime(),
                state.getCaseRemainTime(), workerStates);
    }

    public void setState(@Nullable State state) {
        this.state = state;
    }

    @Override
    public Report start(long flightId) {
        ThreadContext.put("bomberApp", name);
        ThreadContext.put("flightId", String.valueOf(flightId));

        List<String> testSuiteNames = suites.stream()
                .map(testSuite -> String.format("%s %s", testSuite.getName(), testSuite.getTestCases()))
                .collect(Collectors.toList());

        log.info("start flight: {} of  test app with {} suites: {}", flightId, suites.size(), testSuiteNames);

        ScheduledExecutorService watcherEx = Executors.newSingleThreadScheduledExecutor();

        env.getWatchers()
                .forEach(config -> {
                    Watcher watcher = config.getWatcher();
                    watcherEx.scheduleAtFixedRate(() -> watcher.watch(this), 0, config.getPeriod(), config.getTimeUnit());
                });

        Instant startTime = Instant.now();

        env.getWatchers().stream()
                .map(WatcherConfig::getWatcher)
                .forEach(Watcher::startUp);

        env.getSinks()
                .forEach(Sink::startUp);


        try {
            suites.forEach(suite -> suite.run(this));
            state = null;
        } catch (Throwable th) {
            log.error("unexpected throwable", th);
        }

        env.getSinks()
                .forEach(Sink::shutDown);

        env.getWatchers().stream()
                .map(WatcherConfig::getWatcher)
                .forEach(Watcher::shutDown);

        watcherEx.shutdown();

        Instant finishTime = Instant.now();
        endLatch.countDown();
        endLatch = new CountDownLatch(1);
        pool.shutdown();

        ThreadContext.clearAll();
        return new Report(startTime, finishTime);
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
        return suites.stream()
                .map(this::toTestSuite)
                .collect(Collectors.toList());
    }

    private TestSuiteModel toTestSuite(TestSuite<?> suite) {
        List<TestCaseModel> testCases = suite.getTestCases().stream()
                .map(name -> new TestCaseModel(name))
                .collect(Collectors.toList());

        return new TestSuiteModel(suite.getName(), testCases, suite.getSettings(), suite.getWarmUp());
    }

    public boolean isStop() {
        return endLatch.getCount() == 0;
    }
}
