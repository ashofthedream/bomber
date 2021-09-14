package ashes.of.bomber;

import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.flight.TestFlightReport;
import ashes.of.bomber.runner.TestApp;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.watcher.Watcher;

import java.util.List;
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


    public Bomber add(Class<?> app) {
        return add(TestAppBuilder.create(app));
    }

    public Bomber add(TestAppBuilder builder) {
        return add(builder.build());
    }

    public Bomber add(TestApp app) {
        applications.add(app);

        // todo temp
        sinks.forEach(app::add);
        watchers.forEach(watcher -> app.add(1000, watcher));
        return this;
    }

    public List<TestFlightReport> start() {
        return applications.stream()
                .map(TestApp::start)
                .collect(Collectors.toList());
    }
}
