package ashes.of.bomber.watchdog;

import ashes.of.bomber.runner.Runner;
import ashes.of.bomber.watcher.Watcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;


public class Watchdog {
    private static final Logger log = LogManager.getLogger();

    private final Runner<?> runner;
    private final List<Watcher> watchers;
    private final CountDownLatch startLatch;
    private final CountDownLatch endLatch;


    public Watchdog(Runner<?> runner, List<Watcher> watchers, CountDownLatch startLatch, CountDownLatch endLatch) {
        this.runner = runner;
        this.watchers = watchers;
        this.startLatch = startLatch;
        this.endLatch = endLatch;
    }

    private void watch() {
        watchers.forEach(watcher -> watcher.onStart(runner.getState()));

        try {
            startLatch.await(60, SECONDS);
        } catch (InterruptedException e) {
            log.error("watchdog stage: {}, testCase: {}. Start latch is broken, check the count on the end",
                    runner.getState().getStage(), runner.getState().getTestCase());
        }

        while (true) {
            watchers.forEach(watcher -> watcher.watch(runner.getState()));

            if (endLatch.getCount() < 1)
                break;

            try {
                Thread.sleep(5_000);
            } catch (InterruptedException ignore) {
            }
        }

        watchers.forEach(watcher -> watcher.onEnd(runner.getState()));
    }


    public void startInNewThread() {
        Thread thread = new Thread(this::watch);
        thread.setDaemon(true);
        thread.setName(String.format("%s-watchdog", runner.getState().getTestCase()));
        thread.start();
    }
}
