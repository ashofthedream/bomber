package ashes.of.bomber.sink.histogram;

import ashes.of.bomber.flight.Stage;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.tools.Record;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;


public class HistogramTimelineSink implements Sink {
    private static final Logger log = LogManager.getLogger();


    // because I want to obtain resolution between 1s and 1ms...
    private static class DurationTemporalUnit implements TemporalUnit {

        private final Duration duration;

        private DurationTemporalUnit(Duration duration) {
            this.duration = duration;
        }

        @Override
        public Duration getDuration() {
            return duration;
        }

        @Override
        public boolean isDurationEstimated() {
            return true;
        }

        @Override
        public boolean isDateBased() {
            return false;
        }

        @Override
        public boolean isTimeBased() {
            return true;
        }

        @Override
        public <R extends Temporal> R addTo(R temporal, long amount) {
            return (R) temporal.plus(amount, this);
        }

        @Override
        public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
            return temporal1Inclusive.until(temporal2Exclusive, this);
        }
    }

    private final Map<MeasurementKey, NavigableMap<Instant, Measurements>> timeline = new ConcurrentHashMap<>();
    private final TemporalUnit resolution;
    private final HistogramTimelinePrinter printer;

    public HistogramTimelineSink(Duration resolution, HistogramTimelinePrinter printer) {
        this.resolution = new DurationTemporalUnit(resolution);
        this.printer = printer;
    }

    public HistogramTimelineSink(ChronoUnit resolution, HistogramTimelinePrinter printer) {
        this.resolution = resolution;
        this.printer = printer;
    }

    public HistogramTimelineSink() {
        this(ChronoUnit.MINUTES, new HistogramTimelinePrintStreamPrinter());
    }

    @Override
    public void timeRecorded(Record record) {
        var it = record.getIteration();
        var ts = it.getTimestamp().truncatedTo(resolution);
        var key = new MeasurementKey(it.getTestSuite(), it.getTestCase(), it.getStage());
        timeline.computeIfAbsent(key, k -> new ConcurrentSkipListMap<>())
                .computeIfAbsent(ts, timestamp -> new Measurements(key))
                .add(record);
    }

    @Override
    public void afterTestCase(Stage stage, String testSuite, String testCase) {
        var key = new MeasurementKey(testSuite, testCase, stage);
        var measurements = timeline.get(key);
        if (measurements != null) {
            printer.print(resolution, measurements);
        }
    }

    @Override
    public void shutDown(Instant timestamp) {
        printer.print(resolution, timeline);

        timeline.clear();
    }


    public NavigableMap<Instant, Measurements> getTimeline(MeasurementKey key) {
        var measurements = timeline.get(key);
        if (measurements != null) {
            return measurements;
        }

        return new TreeMap<>();
    }
}
