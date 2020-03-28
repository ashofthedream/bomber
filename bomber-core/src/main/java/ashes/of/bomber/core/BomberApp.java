package ashes.of.bomber.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BomberApp {

    String getName();
    List<TestSuiteModel> getTestSuites();

    Report run();

    default CompletableFuture<Report> runAsync() {
        return CompletableFuture.supplyAsync(this::run);
    }

    void await() throws InterruptedException;

    void shutdown();

}
