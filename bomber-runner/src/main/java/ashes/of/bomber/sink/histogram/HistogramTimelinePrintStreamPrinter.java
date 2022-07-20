package ashes.of.bomber.sink.histogram;

import ashes.of.bomber.core.Test;
import org.HdrHistogram.Histogram;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;

public class HistogramTimelinePrintStreamPrinter implements HistogramTimelinePrinter {
    private static final Logger log = LogManager.getLogger();

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
            .withZone(ZoneId.systemDefault());

    public static final String HEAD_FORMAT = "%-12s | %-75s | %11s %11s %11s %11s %11s %11s %11s | %10s %10s%n";
    public static final String LINE_FORMAT = "%-12s | %-75s | %11.3f %11.3f %11.3f %11.3f %11.3f %11.3f %11.3f | %,10d %,10d %n";


    private final PrintStream out;

    public HistogramTimelinePrintStreamPrinter(PrintStream out) {
        this.out = out;
    }

    public HistogramTimelinePrintStreamPrinter() {
        this(System.out);
    }

    @Override
    public void print(TemporalUnit resolution, Map<Test, NavigableMap<Instant, Measurements>> timeline) {
        out.printf("Histogram Timeline, resolution: %s%n", resolution.getDuration());
        if (timeline.isEmpty()) {
            log.warn("Nothing to print, timeline is empty");
            out.println("No Measurements");
            printBorder();
            return;
        }

        timeline.values().stream()
                .sorted(Comparator.comparing(SortedMap::firstKey))
                .forEach(measurements -> print(resolution, measurements));

        out.println();
        out.println();
    }

    @Override
    public void print(TemporalUnit resolution, NavigableMap<Instant, Measurements> timeline) {
        Instant time = timeline.firstKey();
        Instant end = timeline.lastKey();

        printBorder();
        var key = timeline.firstEntry().getValue().getTest();
        out.printf("%s -> %s -> %s%n", key.testApp(), key.testSuite(), key.testCase());
        out.printf("rows: %s, duration: %4.3fs%n",
                timeline.size(),
                (end.toEpochMilli() - time.toEpochMilli()) / 1000.0 );

        out.println();
        out.printf(HEAD_FORMAT, "time", "label", "median", "75.00", "90.00", "95.00", "99.00", "99.90", "max", "count", "errors");

        printBorder();
        while (!time.isAfter(end)) {
            Measurements m = timeline.get(time);
            print(time, m);

            time = time.plus(resolution.getDuration());
        }
        printBorder();
    }

    private void printBorder() {
        out.println("-".repeat(200));
    }


    private void print(Instant time, @Nullable Measurements measurements) {
        if (measurements == null) {
            printEmptyRow(time);
            return;
        }

        var histograms = measurements.getHistograms();
        if (histograms.isEmpty()) {
            printEmptyRow(time);
            return;
        }

        histograms.forEach((label, tae) -> printRow(time, label, tae.getHistogram(), tae.getErrorsCount()));
    }

    private void printEmptyRow(Instant time) {
        out.printf("%-14s | %n", formatter.format(time));
    }

    private void printRow(Instant time, String label, Histogram h, long errorsCount) {
        out.printf(LINE_FORMAT,
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
