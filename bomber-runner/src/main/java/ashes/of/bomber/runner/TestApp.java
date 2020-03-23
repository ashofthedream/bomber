package ashes.of.bomber.runner;

import ashes.of.bomber.core.BomberApp;
import ashes.of.bomber.core.Report;
import ashes.of.bomber.core.State;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.watcher.Watcher;
import ashes.of.bomber.watcher.WatcherConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;


public class TestApp implements BomberApp {
    private static final Logger log = LogManager.getLogger();

    private final Environment environment;
    private final List<TestSuite<?>> suites;

    @Nullable
    private volatile State state;
    private volatile CountDownLatch shutdown = new CountDownLatch(1);

    public TestApp(Environment environment, List<TestSuite<?>> suites) {
        this.environment = environment;
        this.suites = suites;
    }

    public void setState(@Nullable State state) {
        this.state = state;
    }

    @Override
    public Report run() {
        List<String> testSuiteNames = suites.stream()
                .map(testSuite -> String.format("%s %s", testSuite.getName(), testSuite.getTestCases()))
                .collect(Collectors.toList());

        log.info("run test app with {} suites: {}", suites.size(), testSuiteNames);

        ScheduledExecutorService watcherEx = Executors.newSingleThreadScheduledExecutor();

        environment.getWatchers()
                .forEach(config -> {
                    Watcher watcher = config.getWatcher();
                    watcherEx.scheduleAtFixedRate(() -> {
                        State state = this.state;
                        if (state != null)
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
        shutdown.countDown();
        return new Report(startTime, finishTime, errorsCount.sum());
    }

    @Override
    public void await() throws InterruptedException {
        shutdown.await();
    }

    @Override
    public void shutdown() {
        log.info("shutdown");
        shutdown.countDown();
    }

    public boolean isShutdown() {
        return shutdown.getCount() == 0;
    }
}
