package ashes.of.bomber.sink.histo;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;
import ashes.of.bomber.core.stopwatch.Record;
import ashes.of.bomber.sink.Sink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;


public class HistogramTimelineSink implements Sink {
    private static final Logger log = LogManager.getLogger();


    public static class TestStats {
        public final String name;
        public final NavigableMap<Instant, NavigableMap<String, HistogramAndErrors>> timeline = new ConcurrentSkipListMap<>();

        private TestStats(String name) {
            this.name = name;
        }

        public void record(Instant ts, Record record) {
            HistogramAndErrors stats = timeline
                    .computeIfAbsent(ts, k -> new ConcurrentSkipListMap<>())
                    .computeIfAbsent(record.getLabel(), l -> new HistogramAndErrors());

            stats.record(record);
        }
    }

    private final HistogramTimelinePrinter printer;
    private final ChronoUnit resolution;
    private final Map<String, TestStats> tests = new ConcurrentSkipListMap<>();


    public HistogramTimelineSink(HistogramTimelinePrinter printer, ChronoUnit resolution) {
        this.printer = printer;
        this.resolution = resolution;
    }

    public HistogramTimelineSink() {
        this(new HistogramTimelinePrinter(), ChronoUnit.MINUTES);
    }

    @Override
    public void beforeTestSuite(Stage stage, String testCase, Instant startTime, Settings settings) {
        tests.clear();
    }

    @Override
    public void timeRecorded(Context context, Record record) {
        tests.computeIfAbsent(context.getTestCase(), TestStats::new)
                .record(context.getTimestamp().truncatedTo(resolution), record);
    }

    @Override
    public void afterShutdown() {
        printer.print(tests, resolution);
    }
}
