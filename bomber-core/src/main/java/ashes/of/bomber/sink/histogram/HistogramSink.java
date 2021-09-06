package ashes.of.bomber.sink.histogram;

import ashes.of.bomber.flight.Stage;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.tools.Record;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HistogramSink implements Sink {

    /**
     * Measurements by testSuite and testCase
     */
    private final Map<MeasurementKey, Measurements> measurements = new ConcurrentHashMap<>();

    private final HistogramPrinter printer;

    public HistogramSink(HistogramPrinter printer) {
        this.printer = printer;
    }

    public HistogramSink() {
        this(new HistogramPrintStreamPrinter());
    }

    public void timeRecorded(Record record) {
        var it = record.getIteration();
        measurements
                .computeIfAbsent(new MeasurementKey(it.getTestSuite(), it.getTestCase(), it.getStage()), Measurements::new)
                .add(record);
    }

    @Override
    public void afterTestCase(Stage stage, String testSuite, String testCase) {
        var key = new MeasurementKey(testSuite, testCase, stage);
        var measurements = this.measurements.get(key);
        if (measurements != null) {
            printer.print(key, measurements);
        }
    }

    @Override
    public void shutDown(Instant timestamp) {
        measurements.forEach(printer::print);

        measurements.clear();
    }
}
