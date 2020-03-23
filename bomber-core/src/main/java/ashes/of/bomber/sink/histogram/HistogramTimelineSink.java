package ashes.of.bomber.sink.histogram;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.stopwatch.Record;
import ashes.of.bomber.sink.Sink;
import org.HdrHistogram.Histogram;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;


public class HistogramTimelineSink implements Sink {
    private static final Logger log = LogManager.getLogger();

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.S")
            .withZone(ZoneId.systemDefault());


    private final ChronoUnit resolution;
    private final PrintStream out;

    private final NavigableMap<Instant, Measurements> timeline = new ConcurrentSkipListMap<>();


    public HistogramTimelineSink(ChronoUnit resolution, PrintStream out) {
        this.resolution = resolution;
        this.out = out;
    }

    public HistogramTimelineSink() {
        this(ChronoUnit.MINUTES, System.out);
    }

    @Override
    public void timeRecorded(Context context, Record record) {
        Instant ts = context.getTimestamp().truncatedTo(resolution);
        timeline.computeIfAbsent(ts, Measurements::new).add(record);
    }

    @Override
    public void beforeShutDown() {
        print();

        timeline.clear();
    }

    private void print() {
        Instant time = timeline.firstKey();
        Instant end = timeline.lastKey();

        out.printf("%-14s %-50s %11s %11s %11s %11s %11s %11s %11s %10s %10s%n", "time", "label", "median", "75.00", "90.00", "95.00", "99.00", "99.90", "max", "count", "errors");
        while (time.isBefore(end) || time.equals(end)) {
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
