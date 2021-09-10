package ashes.of.bomber.sink;

import ashes.of.bomber.flight.Iteration;
import ashes.of.bomber.configuration.Settings;
import ashes.of.bomber.flight.Stage;
import ashes.of.bomber.tools.Record;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;

public class MultiSink implements Sink {

    private final List<Sink> sinks;

    public MultiSink(List<Sink> sinks) {
        this.sinks = sinks;
    }

    @Override
    public void startUp(Instant timestamp) {
        sinks.forEach(sink -> sink.startUp(timestamp));
    }

    @Override
    public void beforeTestSuite(Instant timestamp, String testSuite) {
        sinks.forEach(sink -> sink.beforeTestSuite(timestamp, testSuite));
    }

    @Override
    public void beforeTestCase(Instant timestamp, Stage stage, String testSuite, String testCase, Settings settings) {
        sinks.forEach(sink -> sink.beforeTestCase(timestamp, stage, testSuite, testCase, settings));
    }

    @Override
    public void timeRecorded(Record record) {
        sinks.forEach(sink -> sink.timeRecorded(record));
    }

    @Override
    public void afterEach(Iteration it, long elapsed, @Nullable Throwable throwable) {
        sinks.forEach(sink -> sink.afterEach(it, elapsed, throwable));
    }

    @Override
    public void afterTestCase(Stage stage, String testSuite, String testCase) {
        sinks.forEach(sink -> sink.afterTestCase(stage, testSuite, testCase));
    }

    @Override
    public void afterTestSuite(String testSuite) {
        sinks.forEach(sink -> sink.afterTestSuite(testSuite));
    }

    @Override
    public void shutDown(Instant timestamp) {
        sinks.forEach(sink -> sink.shutDown(timestamp));
    }
}
