package ashes.of.bomber.sink.histogram;

import ashes.of.bomber.core.Test;
import ashes.of.bomber.events.TestAppFinishedEvent;
import ashes.of.bomber.events.TestCaseFinishedEvent;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.tools.Record;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HistogramSink implements Sink {

    /**
     * Measurements by testSuite and testCase
     */
    private final Map<Test, Measurements> measurements = new ConcurrentHashMap<>();

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
                .computeIfAbsent(record.getIteration().getTest(), Measurements::new)
                .add(record);
    }

    @Override
    public void afterTestCase(TestCaseFinishedEvent event) {

        var measurements = this.measurements.get(event.getTest());
        if (measurements != null) {
            printer.print(event.getTest(), measurements);
        }
    }

    @Override
    public void afterTestApp(TestAppFinishedEvent event) {
        measurements.forEach(printer::print);

        measurements.clear();
    }
}
