package ashes.of.trebuchet.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;


public class Watchdog {
    private static final Logger log = LogManager.getLogger();

    private final Runner<?> runner;
    private final CountDownLatch startLatch;
    private final CountDownLatch endLatch;


    public Watchdog(Runner<?> runner, CountDownLatch startLatch, CountDownLatch endLatch) {
        this.runner = runner;
        this.startLatch = startLatch;
        this.endLatch = endLatch;
    }

    private void watch() {
        try {
            startLatch.await(60, SECONDS);
        } catch (InterruptedException e) {
            log.error("watchdog stage: {}, testCase: {}. Start latch is broken, check the count on the end", runner.getStage(), runner.getTestCaseName());
        }

        while (true) {
            log.info("watchdog stage: {}, testCase: {}, time elapsed: {}ms, remain time: {}ms, remain iterations: {},  errors: {}",
                    runner.getStage(), runner.getTestCaseName(), runner.getElapsedTime(), runner.getRemainTime(), runner.getRemainOps(), runner.getErrorCount());

            if (endLatch.getCount() < 1)
                break;

            try {
                Thread.sleep(10_000);
            } catch (InterruptedException ignore) {
            }
        }

        log.warn("watchdog stage: {}, testCase: {}. Stage is over", runner.getStage(), runner.getTestCaseName());
    }


    public void startInNewThread() {
        Thread thread = new Thread(this::watch);
        thread.setDaemon(true);
        thread.setName(String.format("%s-watchdog", runner.getTestCaseName()));
        thread.start();
    }
}
