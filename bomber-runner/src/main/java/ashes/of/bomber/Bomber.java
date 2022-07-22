package ashes.of.bomber;

import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.core.TestApp;
import ashes.of.bomber.core.TestSuite;
import ashes.of.bomber.events.EventMachine;
import ashes.of.bomber.flight.plan.TestAppPlan;
import ashes.of.bomber.flight.plan.TestCasePlan;
import ashes.of.bomber.flight.plan.TestFlightPlan;
import ashes.of.bomber.flight.plan.TestSuitePlan;
import ashes.of.bomber.flight.report.TestFlightReport;
import ashes.of.bomber.runner.Runner;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.snapshots.TestFlightSnapshot;
import ashes.of.bomber.watcher.Watcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;


import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class Bomber {
    private static final Logger log = LogManager.getLogger();

    @Nullable
    private volatile Runner runner;

    private final EventMachine em = new EventMachine();
    private final List<Watcher> watchers = new CopyOnWriteArrayList<>();
    private final List<TestApp> apps;

    public Bomber(List<Sink> sinks, List<Watcher> watchers, List<TestApp> apps) {
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
        // todo check that application isn't idle
        log.info("Init runner");
        var r = new Runner(em, watchers, apps);

        this.runner = r;

        try {
            return r.run(flightPlan);
        } finally {
            log.info("Flight is over, report is ready");
            ThreadContext.clearAll();
            runner = null;
        }
    }

    public TestFlightReport start() {
        return start(defaultFlightPlan());
    }


    public CompletableFuture<TestFlightReport> startAsync() {
        return CompletableFuture.supplyAsync(this::start);
    }

    public CompletableFuture<TestFlightReport> startAsync(TestFlightPlan plan) {
        return CompletableFuture.supplyAsync(() -> start(plan));
    }

    public void stop() {
        var r = runner;
        if (r != null) {
            r.stop();
        }
    }

    public void await() throws InterruptedException {
        var r = runner;
        if (r != null) {
            r.await();
        }
    }

    @Nullable
    @Deprecated
    public TestFlightSnapshot getSnapshot() {
        var r = runner;
        if (r != null) {
            return r.getSnapshot();
        }

        return null;
    }


    private TestFlightPlan defaultFlightPlan() {
        var apps = this.apps.stream()
                .map(this::defaultTestAppPlan)
                .toList();

        return new TestFlightPlan(System.currentTimeMillis() - 1630454400000L, apps);
    }

    private TestAppPlan defaultTestAppPlan(TestApp app) {
        var suites = app.getTestSuites().stream()
                .map(this::defaultTestSuitePlan)
                .toList();

        return new TestAppPlan(app.getName(), suites);
    }

    private TestSuitePlan defaultTestSuitePlan(TestSuite<?> testSuite) {
        List<TestCasePlan> testCases = testSuite.getTestCases().stream()
                .map(testCase -> new TestCasePlan(testCase.getName(), testCase.getConfiguration()))
                .toList();

        return new TestSuitePlan(testSuite.getName(), testCases);
    }

}
