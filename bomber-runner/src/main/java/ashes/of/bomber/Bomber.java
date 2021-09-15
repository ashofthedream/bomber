package ashes.of.bomber;

import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.plan.TestFlightPlan;
import ashes.of.bomber.report.TestFlightReport;
import ashes.of.bomber.core.TestApp;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.watcher.Watcher;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Bomber {
    private final List<Sink> sinks;
    private final List<Watcher> watchers;
    private final List<TestApp> applications;

    public Bomber(List<Sink> sinks, List<Watcher> watchers, List<TestApp> applications) {
        this.sinks = sinks;
        this.watchers = watchers;
        this.applications = applications;
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
        return applications;
    }

    public Bomber add(Class<?> app) {
        return add(TestAppBuilder.create(app));
    }

    public Bomber add(TestAppBuilder builder) {
        return add(builder.build());
    }

    public Bomber add(TestApp app) {
        applications.add(app);
        return this;
    }

    public TestFlightReport start(TestFlightPlan plan) {
        Instant startTime = Instant.now();
        var reports = applications.stream()
                .map(app -> {
                    app.setSinks(sinks);
                    app.setWatchers(watchers);

                    return app.start(plan);
                })
                .collect(Collectors.toList());
        Instant finishTime = Instant.now();

        return new TestFlightReport(plan, startTime, finishTime, reports);
    }

    public TestFlightReport start() {
        Instant startTime = Instant.now();
        var reports = applications.stream()
                .map(app -> {
                    app.setSinks(sinks);
                    app.setWatchers(watchers);
                    return app.start();
                })
                .collect(Collectors.toList());
        Instant finishTime = Instant.now();

        var plan = new TestFlightPlan(System.currentTimeMillis(), List.of());
        return new TestFlightReport(plan, startTime, finishTime, reports);
    }


    public CompletableFuture<TestFlightReport> startAsync(TestFlightPlan plan) {
        return CompletableFuture.supplyAsync(() -> start(plan));
    }

    public void stop() {
        applications.forEach(TestApp::stop);
    }
}
