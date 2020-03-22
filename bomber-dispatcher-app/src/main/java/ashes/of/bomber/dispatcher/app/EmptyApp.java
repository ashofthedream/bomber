package ashes.of.bomber.dispatcher.app;

import ashes.of.bomber.core.Application;
import ashes.of.bomber.core.Report;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;

public class EmptyApp implements Application {

    private final CountDownLatch shutdown = new CountDownLatch(1);

    @Override
    public Report run() {
        shutdown.countDown();
        return new Report(Instant.now(), Instant.now(), 0);
    }

    @Override
    public void await() throws InterruptedException {
        shutdown.await();
    }

    @Override
    public void shutdown() {
        shutdown.countDown();
    }
}
