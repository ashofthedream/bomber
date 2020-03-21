package ashes.of.bomber.sink.histo;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.stopwatch.Record;
import ashes.of.bomber.sink.Sink;
import org.HdrHistogram.Histogram;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HistogramSink implements Sink {

    private final Map<String, Map<String, HistogramAndErrors>> histograms = new ConcurrentHashMap<>();

    public void timeRecorded(Context context, Record record) {
        histograms
                .computeIfAbsent(context.getTestSuite(), name -> new ConcurrentHashMap<>())
                .computeIfAbsent(record.getLabel(), label -> new HistogramAndErrors())
                .record(record);
    }

    @Override
    public void shutDown() {
        histograms.forEach((testSuite, map) -> {

            System.out.printf("testSuite: %s%n", testSuite);
            map.forEach((label, hae) -> {
                System.out.printf("testSuite: %s, label: %s, errors: %,12d%n", testSuite, label, hae.errors.sum());
                Histogram copy = hae.histogram.copy();
                copy.outputPercentileDistribution(System.out, 1_000_000.0);

                System.out.println();
                System.out.println();
            });
        });
    }
}
