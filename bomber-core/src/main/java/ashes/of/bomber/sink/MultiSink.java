package ashes.of.bomber.sink;

import ashes.of.bomber.core.Iteration;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;
import ashes.of.bomber.stopwatch.Record;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;

public class MultiSink implements Sink {

    private final List<Sink> sinks;

    public MultiSink(List<Sink> sinks) {
        this.sinks = sinks;
    }

    @Override
    public void afterStartUp() {
        sinks.forEach(Sink::afterStartUp);
    }

    @Override
    public void beforeTestSuite(Stage stage, String testSuite, Instant timestamp, Settings settings) {
        sinks.forEach(sink -> sink.beforeTestSuite(stage, testSuite, timestamp, settings));
    }

    @Override
    public void beforeTestCase(Stage stage, String testSuite, String testCase, Instant timestamp, Settings settings) {
        sinks.forEach(sink -> sink.beforeTestCase(stage, testSuite, testCase, timestamp, settings));
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
    public void afterTestSuite(Stage stage, String testSuite) {
        sinks.forEach(sink -> sink.afterTestSuite(stage, testSuite));
    }

    @Override
    public void beforeShutDown() {
        sinks.forEach(Sink::beforeShutDown);
    }
}
