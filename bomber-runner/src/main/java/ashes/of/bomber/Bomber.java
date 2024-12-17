package ashes.of.bomber;

import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.configuration.Configuration;
import ashes.of.bomber.core.TestSuiteGroup;
import ashes.of.bomber.core.TestSuite;
import ashes.of.bomber.events.EventMachine;
import ashes.of.bomber.flight.plan.TestCasePlan;
import ashes.of.bomber.flight.plan.TestFlightPlan;
import ashes.of.bomber.flight.plan.TestSuitePlan;
import ashes.of.bomber.runner.TestFlightTask;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.snapshots.TestFlightSnapshot;
import ashes.of.bomber.threads.BomberThreadFactory;
import ashes.of.bomber.watcher.Watcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;


import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bomber {
    private static final Logger log = LogManager.getLogger();

    @Nullable
    private volatile TestFlightTask testFlightTask;

    private final ScheduledExecutorService watcherEx = Executors.newSingleThreadScheduledExecutor(BomberThreadFactory.WATCHER_FACTORY);

    private final EventMachine em = new EventMachine();
    private final List<Watcher> watchers = new CopyOnWriteArrayList<>();
    private final List<TestSuiteGroup> apps;

    public Bomber(List<Sink> sinks, List<Watcher> watchers, List<TestSuiteGroup> apps) {
        sinks.forEach(this::addSink);
        watchers.forEach(this::addWatcher);
        this.apps = apps;
    }

    public Bomber addSink(Sink sink) {
        sink.configure(em);
        return this;
    }

    public Bomber addWatcher(Watcher watcher) {
        watchers.add(watcher);
        return this;
    }


    public List<TestSuiteGroup> getApps() {
        return apps;
    }

    public Bomber add(Class<?> app) {
        return add(TestAppBuilder.create(app));
    }

    public Bomber add(TestAppBuilder builder) {
        return add(builder.build());
    }

    public Bomber add(TestSuiteGroup app) {
        apps.add(app);
        return this;
    }

    public void start(TestFlightPlan plan) {
        var current = testFlightTask;
        if (current != null) {
            throw new IllegalStateException("Invalid runner state: already exists for flightId: " + current.getPlan().flightId());
        }

        log.info("Start flight: {} with plan:", plan.flightId());
        printFlightPlan(plan);

        // todo check that application isn't idle
        log.debug("Start watchers, watch every {}s", 1);
        var wfs = watchers.stream()
                .map(watcher -> watcherEx.scheduleAtFixedRate(() -> watcher.watch(getSnapshot()), 0, 1, TimeUnit.SECONDS))
                .toList();

        var task = new TestFlightTask(plan, em, apps);
        this.testFlightTask = task;

        try {
            task.run();
        } finally {
            log.debug("Stop watchers");
            wfs.forEach(wf -> wf.cancel(false));

            log.info("Flight is over");
            ThreadContext.clearAll();
            testFlightTask = null;
        }
    }

    public void start() {
        start(defaultFlightPlan());
    }


    public CompletableFuture<Void> startAsync(TestFlightPlan plan) {
        return CompletableFuture.runAsync(() -> start(plan));
    }

    public CompletableFuture<Void> startAsync() {
        return CompletableFuture.runAsync(this::start);
    }

    public void stop() {
        var r = testFlightTask;
        if (r != null) {
            r.stop();
        }
    }

    public void await() throws InterruptedException {
        var r = testFlightTask;
        if (r != null) {
            r.await();
        }
    }

    @Nullable
    @Deprecated
    public TestFlightSnapshot getSnapshot() {
        var task = testFlightTask;
        return task != null ? task.getSnapshot() : null;
    }

    private TestFlightPlan defaultFlightPlan() {
        var apps = this.apps.stream()
                .map(this::defaultTestAppPlan)
                .flatMap(Collection::stream)
                .toList();

        return new TestFlightPlan(System.currentTimeMillis() - 1630454400000L, apps);
    }

    private List<TestSuitePlan> defaultTestAppPlan(TestSuiteGroup app) {
        return app.testSuites().stream()
                .map(this::defaultTestSuitePlan)
                .toList();
    }

    private TestSuitePlan defaultTestSuitePlan(TestSuite<?> testSuite) {
        var testCases = testSuite.getTestCases().stream()
                .map(testCase -> new TestCasePlan(testCase.name(), testCase.configuration()))
                .toList();

        return new TestSuitePlan(testSuite.name(), testCases);
    }


    private static void printFlightPlan(TestFlightPlan flightPlan) {
        flightPlan.testSuites().forEach(testSuite -> {
            log.info("\tTest Suite: {}", testSuite.name());
            testSuite.testCases().forEach(testCase -> {
                var settings = Optional.ofNullable(testCase.configuration())
                        .map(Configuration::settings)
                        .orElse(null);

                log.info("\t\tTest Case: {}, with: {}", testCase.name(), settings);
            });
        });
    }
}
