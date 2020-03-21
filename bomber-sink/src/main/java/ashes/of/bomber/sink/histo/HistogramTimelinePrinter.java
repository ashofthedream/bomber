package ashes.of.bomber.sink.histo;

import org.HdrHistogram.Histogram;

import javax.annotation.Nullable;
import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Predicate;


// todo add overall diagrams
public class HistogramTimelinePrinter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.S")
            .withZone(ZoneId.systemDefault());

    private final PrintStream out;
    private final Predicate<String> filter;

    public HistogramTimelinePrinter(PrintStream out, Predicate<String> filter) {
        this.out = out;
        this.filter = filter;
    }

    public HistogramTimelinePrinter() {
        this(System.out, label -> true);
    }

    public void print(Map<String, HistogramTimelineSink.TestStats> tests, ChronoUnit resolution) {
        tests.forEach((name, stats) -> printTestStats(name, stats.timeline, resolution));
    }

    private void printTestStats(String test, NavigableMap<Instant, NavigableMap<String, HistogramTimelineSink.TimeAndErrors>> timeline, ChronoUnit resolution) {

        Map<String, Histogram> overallByLabel = new TreeMap<>();

        out.println();
        out.printf("test: %s, resolution: %s%n", test, resolution);
        out.printf("%-14s %-40s %16s %16s %16s %16s %16s %16s %16s %16s %16s%n", "time", "label", "median", "75.00", "90.00", "95.00", "99.00", "99.90", "max", "count", "errors");

        Instant time = timeline.firstKey();
        Instant end = timeline.lastKey();

        while (time.isBefore(end) || time.equals(end)) {
            NavigableMap<String, HistogramTimelineSink.TimeAndErrors> times = timeline.get(time);
            printForTime(time, times);

            time = time.plus(resolution.getDuration());
        }

        out.println();
        out.println();
    }

    private void printForTime(Instant ts, @Nullable NavigableMap<String, HistogramTimelineSink.TimeAndErrors> times) {
        if (times == null) {
            printEmpty(ts);
            return;
        }

        times.forEach((label, timeAndErrors) -> {
            if (filter.test(label))
                printRow(label, ts, timeAndErrors.histogram, timeAndErrors.errors.sum());
        });
    }

    private void printEmpty(Instant ts) {
        out.printf("%-14s %n", formatter.format(ts));
    }

    private void printRow(String label, Instant ts, Histogram h, long errorsCount) {
        out.printf("%-14s %-40s %16.3f %16.3f %16.3f %16.3f %16.3f %16.3f %16.3f %,16d %,16d%n",
                formatter.format(ts), label,
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
