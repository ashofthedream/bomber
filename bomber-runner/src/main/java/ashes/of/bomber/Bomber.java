package ashes.of.bomber;

import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.core.TestApp;
import ashes.of.bomber.flight.plan.TestFlightPlan;
import ashes.of.bomber.flight.report.TestFlightReport;
import ashes.of.bomber.runner.Runner;
import ashes.of.bomber.runner.RunnerState;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.watcher.Watcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class Bomber {
    private static final Logger log = LogManager.getLogger();

    private volatile CountDownLatch endLatch = new CountDownLatch(1);

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
        // todo check that application isn't idle

        endLatch = new CountDownLatch(1);

        log.info("Init runner");
        RunnerState state = new RunnerState(() -> endLatch.getCount() == 0);
        Runner runner = new Runner(state, sinks, watchers, apps);

        try {
            return runner.run(flightPlan);
        } finally {
            log.info("Flight is over, report is ready");
            ThreadContext.clearAll();
            endLatch.countDown();
            endLatch = null;
        }
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
        var latch = this.endLatch;
        if (latch != null) {
            latch.await();
        }
    }
}
