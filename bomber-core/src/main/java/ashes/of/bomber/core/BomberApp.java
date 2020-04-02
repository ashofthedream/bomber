package ashes.of.bomber.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BomberApp {

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
