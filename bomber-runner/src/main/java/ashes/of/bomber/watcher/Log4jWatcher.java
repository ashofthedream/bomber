package ashes.of.bomber.watcher;

import ashes.of.bomber.snapshots.TestFlightSnapshot;
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
        var testApp = flight.current();
        if (testApp == null) {
            log.info("No test app running...");
            return;
        }

        ThreadContext.put("testApp", testApp.name());

        var testSuite = testApp.current();
        if (testSuite == null) {
            log.info("No test suite running...");
            return;
        }

        ThreadContext.put("testSuite", testSuite.name());

        var testCase = testSuite.current();
        if (testCase == null) {
            log.info("No test case running...");
            return;
        }

        ThreadContext.put("testCase", testCase.name());


        var settings = testCase.settings();
        long totalIts = settings.totalIterationsCount();
        long currentIts = testCase.currentIterationsCount();

        long expectedCount = flight.workers()
                .stream()
                .mapToLong(WorkerSnapshot::expectedRecordsCount)
                .sum();

        long caughtCount = flight.workers()
                .stream()
                .mapToLong(WorkerSnapshot::caughtRecordsCount)
                .sum();

        long watcherErrorCount = flight.workers()
                .stream()
                .mapToLong(WorkerSnapshot::errorsCount)
                .sum();

        double totalSecs = settings.duration().getSeconds();
        double elapsedSecs = (testCase.startTime().toEpochMilli() / 1000.0);

        StringBuilder tp = new StringBuilder();
        StringBuilder ip = new StringBuilder();

        double count = 50;
        for (int i = 0; i < count; i++) {
            ip.append(currentIts > (i / count * totalIts) ? 'x' : '.');
            tp.append(elapsedSecs > (i / count * totalSecs) ? 'x' : '.');
        }

        RpsMeter itRps = this.its.updateAndGet(current -> new RpsMeter(currentIts, current));
        RpsMeter recRps = this.recs.updateAndGet(current -> new RpsMeter(caughtCount, current));

        log.info("%n%-12s %12.1fs [%s] %12.1fs " +
                        "%n%-12s %,12d  [%s] %,12d  %,12d " +
                        "%n%-12s %12.2f rq/s %12.2f rec/s (%8d, %8d)",
                "time",
                elapsedSecs,
                tp.toString(),
                totalSecs,

                "iterations",
                currentIts,
                ip.toString(),
                totalIts,
                watcherErrorCount,

                "meta",
                itRps.rps(),
                recRps.rps(),
                expectedCount,
                caughtCount);

        ThreadContext.clearAll();
    }
}
