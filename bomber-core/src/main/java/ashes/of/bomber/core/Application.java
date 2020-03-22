package ashes.of.bomber.core;

import java.util.concurrent.CompletableFuture;

public interface Application {
    Report run();

    default CompletableFuture<Report> runAsync() {
        return CompletableFuture.supplyAsync(this::run);
    }

    void await() throws InterruptedException;
    void shutdown();
}
