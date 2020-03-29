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
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;


public class TestApp implements BomberApp {
    private static final Logger log = LogManager.getLogger();

    private static final State REST = new State(Stage.Rest, new Settings().disabled(), "", () -> false);

    private final String name;
    private final WorkerPool pool;
    private final Environment environment;
    private final List<TestSuite<?>> suites;

    @Nullable
    private volatile State state;
    private volatile CountDownLatch endLatch = new CountDownLatch(1);

    public TestApp(String name, WorkerPool pool, Environment environment, List<TestSuite<?>> suites) {
        this.name = name;
        this.pool = pool;
        this.environment = environment;
        this.suites = suites;
    }

    @Override
    @Nullable
    public State getState() {
        return state;
    }

    public void setState(@Nullable State state) {
        this.state = state;
    }

    @Override
    public Report start() {
        ThreadContext.put("bomberApp", name);
        List<String> testSuiteNames = suites.stream()
                .map(testSuite -> String.format("%s %s", testSuite.getName(), testSuite.getTestCases()))
                .collect(Collectors.toList());

        log.info("run test app with {} suites: {}", suites.size(), testSuiteNames);

        ScheduledExecutorService watcherEx = Executors.newSingleThreadScheduledExecutor();

        environment.getWatchers()
                .forEach(config -> {
                    Watcher watcher = config.getWatcher();
                    watcherEx.scheduleAtFixedRate(() -> {
                        State state = Optional.ofNullable(this.state)
                                .orElse(REST);
                        watcher.watch(state);
                    }, 0, config.getPeriod(), config.getTimeUnit());
                });

        Instant startTime = Instant.now();

        environment.getWatchers().stream()
                .map(WatcherConfig::getWatcher)
                .forEach(Watcher::startUp);

        environment.getSinks()
                .forEach(Sink::afterStartUp);


        LongAdder errorsCount = new LongAdder();
        try {
            suites.stream()
                    .map(suite -> suite.run(this))
                    .forEach(states -> {
                        states.forEach(state -> {
                            errorsCount.add(state.getErrorCount());
                        });
                    });

            state = null;
        } catch (Throwable th) {
            log.error("unexpected throwable", th);
        }

        environment.getSinks()
                .forEach(Sink::beforeShutDown);

        environment.getWatchers().stream()
                .map(WatcherConfig::getWatcher)
                .forEach(Watcher::shutDown);

        watcherEx.shutdown();

        Instant finishTime = Instant.now();
        endLatch.countDown();
        endLatch = new CountDownLatch(1);
        pool.shutdown();

        ThreadContext.clearAll();
        return new Report(startTime, finishTime, errorsCount.sum());
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
