package ashes.of.bomber.sink;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;
import ashes.of.bomber.core.stopwatch.Lap;
import org.HdrHistogram.ConcurrentHistogram;
import org.HdrHistogram.Histogram;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.LongAdder;


public class HistogramSink implements Sink {
    private static final Logger log = LogManager.getLogger();


    public static class Stats {
        public final String name;
        public final NavigableMap<Instant, Histogram> tests = new ConcurrentSkipListMap<>();
        public final NavigableMap<Instant, LongAdder> errors = new ConcurrentSkipListMap<>();
        public final NavigableMap<Instant, NavigableMap<String, Histogram>> laps = new ConcurrentSkipListMap<>();

        private Stats(String name) {
            this.name = name;
        }

        public void lap(Instant ts, Lap.Record record) {
            Histogram h = laps
                    .computeIfAbsent(ts, k -> new ConcurrentSkipListMap<>())
                    .computeIfAbsent(record.getLabel(), l -> new ConcurrentHistogram(2));

            h.recordValue(record.getElapsed());
        }

        public void overall(Instant ts, long elapsed, @Nullable Throwable throwable) {
            tests.computeIfAbsent(ts, k -> new ConcurrentHistogram(2))
                    .recordValue(elapsed);

            if (throwable == null)
                return;

            errors.computeIfAbsent(ts, k -> new LongAdder())
                    .increment();
        }
    }


    private final HistogramSinkPrinter printer;
    private final ChronoUnit resolution;
    private final Map<String, Stats> statsByTest = new ConcurrentSkipListMap<>();


    public HistogramSink(HistogramSinkPrinter printer, ChronoUnit resolution) {
        this.printer = printer;
        this.resolution = resolution;
    }

    public HistogramSink() {
        this(new HistogramSinkPrinter(), ChronoUnit.SECONDS);
    }


    @Override
    public void beforeAll(Stage stage, String testCase, Instant startTime, Settings settings) {
        statsByTest.clear();
    }

    @Override
    public void afterEachLap(Context context, Lap.Record record) {
        statsByTest.computeIfAbsent(context.getTest(), Stats::new)
                .lap(context.getTimestamp().truncatedTo(resolution), record);

    }

    @Override
    public void afterEachTest(Context context, long elapsed, @Nullable Throwable throwable) {
        statsByTest.computeIfAbsent(context.getTest(), Stats::new)
                .overall(context.getTimestamp().truncatedTo(resolution), elapsed, throwable);
    }

    @Override
    public void afterAll(Stage stage, String testCase, Instant startTime, Settings settings) {
        log.info("afterAll, printStats");
        printer.print(statsByTest, resolution);
    }
}
