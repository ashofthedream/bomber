package ashes.of.bomber.sink;

import org.HdrHistogram.ConcurrentHistogram;
import org.HdrHistogram.Histogram;

import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.atomic.LongAdder;

public class HistogramSinkPrinter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.S")
            .withZone(ZoneId.systemDefault());

    private static final LongAdder noErrors = new LongAdder();

    private final PrintStream out;

    public HistogramSinkPrinter(PrintStream out) {
        this.out = out;
    }

    public HistogramSinkPrinter() {
        this(System.out);
    }

    public void print(Map<String, HistogramSink.Stats> statsByTest, ChronoUnit resolution) {
        statsByTest.forEach((name, stats) -> printTestStats(name, stats, resolution));
    }

    private void printTestStats(String name, HistogramSink.Stats stats, ChronoUnit resolution) {
        NavigableMap<Instant, Histogram> times = stats.tests;
        NavigableMap<Instant, LongAdder> errors = stats.errors;
        String label = "Overall test";

        Histogram histogram = new ConcurrentHistogram(2);
        times.values()
                .forEach(histogram::add);

        out.println();
        out.printf("%s: %s, resolution: %s%n", label, name, resolution);
        histogram.outputPercentileDistribution(out, 1_000_000.0);
        out.println();

        out.printf("%-14s %-40s %16s %16s %16s %16s %16s %16s %16s %16s %16s%n", "time", "label", "median", "75.00", "90.00", "95.00", "99.00", "99.90", "max", "count", "errors");

        Instant time = times.firstKey();
        Instant end = times.lastKey();

        while (time.isBefore(end) || time.equals(end)) {
            printForInstant(time, times, errors, stats);
            time = time.plus(resolution.getDuration());
        }

        out.println();
        out.println();
    }

    private void printForInstant(Instant ts, NavigableMap<Instant, Histogram> times, NavigableMap<Instant, LongAdder> errors, HistogramSink.Stats stats) {
        Histogram h = times.get(ts);
        LongAdder e = errors.getOrDefault(ts, noErrors);

        if (h == null) {
            printEmpty(ts);
            return;
        }

        printTestRow(ts, h, e.sum());

        Map<String, Histogram> laps = stats.laps.get(ts);
        if (laps != null)
            laps.forEach((lap, hl) -> printLapRow(ts, lap, hl, -1));
    }

    private void printEmpty(Instant ts) {
        out.printf("%-14s %n", formatter.format(ts));
    }

    private void printTestRow(Instant ts, Histogram h, long errorsCount) {
        out.printf("%-14s %-40s %16.3f %16.3f %16.3f %16.3f %16.3f %16.3f %16.3f %,16d %,16d%n",
                formatter.format(ts), "",
                ms(h.getValueAtPercentile(0.5)),
                ms(h.getValueAtPercentile(0.75)),
                ms(h.getValueAtPercentile(0.90)),
                ms(h.getValueAtPercentile(0.95)),
                ms(h.getValueAtPercentile(0.99)),
                ms(h.getValueAtPercentile(0.999)),
                ms(h.getMaxValue()),
                h.getTotalCount(), errorsCount);
    }

    private void printLapRow(Instant ts, String label, Histogram h, long errorsCount) {
        out.printf("%-14s %-40s %16.3f %16.3f %16.3f %16.3f %16.3f %16.3f %16.3f %,16d %,16d%n",
                "", label,
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
