package ashes.of.bomber.sink.histogram;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.stopwatch.Record;
import ashes.of.bomber.sink.Sink;

import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HistogramSink implements Sink {

    private final Map<String, Measurements> measurements = new ConcurrentHashMap<>();
    private final PrintStream out;

    public HistogramSink(PrintStream out) {
        this.out = out;
    }

    public HistogramSink() {
        this(System.out);
    }

    public void timeRecorded(Context context, Record record) {
        measurements
                .computeIfAbsent(context.getTestSuite(), name -> new Measurements(context.getTimestamp()))
                .add(record);
    }

    @Override
    public void beforeShutDown() {
        print();

        measurements.clear();
    }

    private void print() {
        measurements.forEach((testSuite, m) -> {

            out.printf("suite: %s%n", testSuite);
            m.data.forEach((label, hae) -> {
                out.printf("label: %s, errors: %,12d%n", label, hae.errors.sum());
                hae.histogram.outputPercentileDistribution(out, 1_000_000.0);

                out.println();
                out.println();
            });
        });
    }
}
