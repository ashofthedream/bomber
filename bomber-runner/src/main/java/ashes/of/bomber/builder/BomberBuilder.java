package ashes.of.bomber.builder;

import ashes.of.bomber.Bomber;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.watcher.Watcher;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class BomberBuilder {

    private final List<Sink> sinks = new CopyOnWriteArrayList<>();
    private final List<Watcher> watchers = new CopyOnWriteArrayList<>();
    private final List<TestAppBuilder> applications = new ArrayList<>();


    public BomberBuilder sink(Sink sink) {
        this.sinks.add(sink);
        return this;
    }

    public BomberBuilder sinks(List<Sink> sinks) {
        this.sinks.addAll(sinks);
        return this;
    }

    public BomberBuilder watcher(Watcher watcher) {
        this.watchers.add(watcher);
        return this;
    }

    public BomberBuilder add(Class<?> app) {
        return add(TestAppBuilder.create(app));
    }

    public BomberBuilder add(TestAppBuilder app) {
        this.applications.add(app);
        return this;
    }

    public Bomber build() {
        Preconditions.checkArgument(!applications.isEmpty(), "No applications found");

        var apps = applications.stream()
                .map(TestAppBuilder::build)
                .collect(Collectors.toCollection(CopyOnWriteArrayList::new));

        return new Bomber(sinks, watchers, apps);
    }
}
