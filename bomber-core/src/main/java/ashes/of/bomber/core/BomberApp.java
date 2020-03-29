package ashes.of.bomber.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BomberApp {

    State getState();

    String getName();
    List<TestSuiteModel> getTestSuites();

    Report start();

    default CompletableFuture<Report> startAsync() {
        return CompletableFuture.supplyAsync(this::start);
    }

    void await() throws InterruptedException;

    void stop();

}
