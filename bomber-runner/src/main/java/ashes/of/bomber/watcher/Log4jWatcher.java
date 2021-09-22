package ashes.of.bomber.watcher;

import ashes.of.bomber.configuration.Settings;
import ashes.of.bomber.snapshots.TestAppSnapshot;
import ashes.of.bomber.snapshots.TestFlightSnapshot;
import ashes.of.bomber.snapshots.TestSuiteSnapshot;
import ashes.of.bomber.snapshots.WorkerSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

import java.util.concurrent.atomic.AtomicReference;

public class Log4jWatcher implements Watcher {
    private static final Logger log = LogManager.getLogger(new StringFormatterMessageFactory());

    private static class RpsMeter {
        private final long time;
        private final long total;
        private final long count;
        private final long interval;

        public RpsMeter(long total, RpsMeter previous) {
            this.time = System.currentTimeMillis();
            this.total = total;
            this.count = total - previous.total;
            this.interval = time - previous.time;
        }

        public RpsMeter() {
            this.time = System.currentTimeMillis();
            this.interval = 0;
            this.count = 0;
            this.total = 0;
        }

        public double rps() {
            return count / (interval / 1000.0);
        }
    }

    private final AtomicReference<RpsMeter> its = new AtomicReference<>(new RpsMeter());
    private final AtomicReference<RpsMeter> recs = new AtomicReference<>(new RpsMeter());


    @Override
    public void watch(TestFlightSnapshot flight) {
        var testApp = flight.getCurrent();
        if (testApp == null) {
            log.info("No test app running...");
            return;
        }

        ThreadContext.put("testApp", testApp.getName());

        var testSuite = testApp.getCurrent();
        if (testSuite == null) {
            log.info("No test suite running...");
            return;
        }

        ThreadContext.put("testSuite", testSuite.getName());

        var testCase = testSuite.getCurrent();
        if (testCase == null) {
            log.info("No test case running...");
            return;
        }

        ThreadContext.put("testCase", testCase.getName());


        var settings = testCase.getSettings();
        long totalIterationsCount = settings.getTotalIterationsCount();
        long currentIterationsCount = testCase.getCurrentIterationsCount();

        long expectedCount = flight.getWorkers()
                .stream()
                .mapToLong(WorkerSnapshot::getExpectedRecordsCount)
                .sum();

        long caughtCount = flight.getWorkers()
                .stream()
                .mapToLong(WorkerSnapshot::getCaughtRecordsCount)
                .sum();

        long watcherErrorCount = flight.getWorkers()
                .stream()
                .mapToLong(WorkerSnapshot::getErrorsCount)
                .sum();

        double totalSecs = settings.getDuration().getSeconds();
        double elapsedSecs = (testCase.getStartTime().toEpochMilli() / 1000.0);

        StringBuilder tp = new StringBuilder();
        StringBuilder ip = new StringBuilder();

        double count = 50;
        for (int i = 0; i < count; i++) {
            ip.append(currentIterationsCount > (i / count * totalIterationsCount) ? 'x' : '.');
            tp.append(elapsedSecs > (i / count * totalSecs) ? 'x' : '.');
        }

        RpsMeter itRps = this.its.updateAndGet(current -> new RpsMeter(currentIterationsCount, current));
        RpsMeter recRps = this.recs.updateAndGet(current -> new RpsMeter(caughtCount, current));

        log.info("%n%-12s %12.1fs [%s] %12.1fs " +
                "%n%-12s %,12d  [%s] %,12d  %,12d " +
                "%n%-12s %12.2f rq/s %12.2f rec/s (%8d, %8d)",
                "time",
                elapsedSecs,
                tp.toString(),
                totalSecs,

                "iterations",
                currentIterationsCount,
                ip.toString(),
                totalIterationsCount,
                watcherErrorCount,

                "meta",
                itRps.rps(),
                recRps.rps(),
                expectedCount,
                caughtCount);

        ThreadContext.clearAll();
    }
}
