package ashes.of.bomber.sink.histogram;

import ashes.of.bomber.tools.Record;
import ashes.of.bomber.sink.Sink;
import org.HdrHistogram.Histogram;

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

    public void timeRecorded(Record record) {
        measurements
                .computeIfAbsent(record.getIteration().getTestSuite(), name -> new Measurements(record.getIteration().getTimestamp()))
                .add(record);
    }

    @Override
    public void shutDown() {
        print();

        measurements.clear();
    }

    private void print() {
        measurements.forEach((testSuite, m) -> {

            out.printf("suite: %s%n", testSuite);
            m.data.forEach((label, hae) -> {
                out.printf("label: %s, errors: %,12d%n", label, hae.errors.sum());
                Histogram h = new Histogram(hae.histogram);
                h.outputPercentileDistribution(out, 1_000_000.0);

                out.println();
                out.println();
            });
        });
    }
}
