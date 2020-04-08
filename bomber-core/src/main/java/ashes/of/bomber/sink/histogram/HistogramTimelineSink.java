package ashes.of.bomber.sink.histogram;

import ashes.of.bomber.core.Iteration;
import ashes.of.bomber.stopwatch.Record;
import ashes.of.bomber.sink.Sink;
import org.HdrHistogram.Histogram;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;


public class HistogramTimelineSink implements Sink {
    private static final Logger log = LogManager.getLogger();

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
            .withZone(ZoneId.systemDefault());


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


    private final TemporalUnit resolution;
    private final PrintStream out;

    private final NavigableMap<Instant, Measurements> timeline = new ConcurrentSkipListMap<>();


    public HistogramTimelineSink(Duration resolution, PrintStream out) {
        this.resolution = new DurationTemporalUnit(resolution);
        this.out = out;
    }

    public HistogramTimelineSink(ChronoUnit resolution, PrintStream out) {
        this.resolution = resolution;
        this.out = out;
    }

    public HistogramTimelineSink() {
        this(ChronoUnit.MINUTES, System.out);
    }

    @Override
    public void timeRecorded(Record record) {
        Instant ts = record.getIteration().getTimestamp().truncatedTo(resolution);
        timeline.computeIfAbsent(ts, Measurements::new).add(record);
    }

    @Override
    public void shutDown() {
        print();

        timeline.clear();
    }

    private void print() {
        if (timeline.isEmpty()) {
            log.warn("Nothing to print, timeline is empty");
            return;
        }

        Instant time = timeline.firstKey();
        Instant end = timeline.lastKey();

        out.printf("count: %s, start: %s, end: %s%n", timeline.size(), time, end);
        out.printf("%-14s %-50s %11s %11s %11s %11s %11s %11s %11s %10s %10s%n", "time", "label", "median", "75.00", "90.00", "95.00", "99.00", "99.90", "max", "count", "errors");
        while (!time.isAfter(end)) {
            Measurements measurements = timeline.get(time);
            print(time, measurements);

            time = time.plus(resolution.getDuration());
        }
    }

    private void print(Instant time, @Nullable Measurements measurements) {
        if (measurements == null) {
            printEmptyRow(time);
            return;
        }

        if (measurements.data.isEmpty()) {
            printEmptyRow(time);
            return;
        }

        measurements.data.forEach((label, tae) -> printRow(time, label, tae.histogram, tae.errors.sum()));
    }

    private void printEmptyRow(Instant time) {
        out.printf("%-14s %n", formatter.format(time));
    }

    private void printRow(Instant time, String label, Histogram h, long errorsCount) {
        out.printf("%-14s %-50s %11.2f %11.2f %11.2f %11.2f %11.2f %11.2f %11.2f %,10d %,10d%n",
                formatter.format(time), label,
                ms(h.getValueAtPercentile(0.5)),
                ms(h.getValueAtPercentile(0.75)),
                ms(h.getValueAtPercentile(0.90)),
                ms(h.getValueAtPercentile(0.95)),
                ms(h.getValueAtPercentile(0.99)),
                ms(h.getValueAtPercentile(0.999)),
                ms(h.getMaxValue()),
                h.getTotalCount(), errorsCount);
    }

    private static double ms(long ns) {
        return ns / 1_000_000.0;
    }
}
