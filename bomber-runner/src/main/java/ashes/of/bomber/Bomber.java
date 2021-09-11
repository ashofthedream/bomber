package ashes.of.bomber;

import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.flight.TestFlightReport;
import ashes.of.bomber.runner.TestApp;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.watcher.Watcher;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Bomber {
    private final List<Sink> sinks = new CopyOnWriteArrayList<>();
    private final List<Watcher> watchers = new CopyOnWriteArrayList<>();
    private final List<TestApp> applications = new CopyOnWriteArrayList<>();

    public Bomber sink(Sink sink) {
        sinks.add(sink);
        return this;
    }

    public Bomber watcher(Watcher watcher) {
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
                .map(app -> app.start())
                .collect(Collectors.toList());
    }
}
