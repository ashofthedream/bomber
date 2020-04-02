package ashes.of.bomber.core;

import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.watcher.Watcher;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BomberApp {

    long getFlightId();

    void add(Sink sink);

    void add(long ms, Watcher watcher);

    default void add(Duration duration, Watcher watcher) {
        add(duration.toMillis(), watcher);
    }

    StateModel getState();

    String getName();

    List<TestSuiteModel> getTestSuites();

    Report start(long id);

    default Report start() {
        return start(System.currentTimeMillis());
    }

    default CompletableFuture<Report> startAsync(long id) {
        return CompletableFuture.supplyAsync(() -> start(id));
    }

    void await() throws InterruptedException;

    void stop();


}
