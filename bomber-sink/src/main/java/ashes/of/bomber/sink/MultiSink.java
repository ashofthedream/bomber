package ashes.of.bomber.sink;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;
import ashes.of.bomber.core.stopwatch.Record;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;

public class MultiSink implements Sink {

    private final List<Sink> sinks;

    public MultiSink(List<Sink> sinks) {
        this.sinks = sinks;
    }

    @Override
    public void startUp() {
        sinks.forEach(Sink::startUp);
    }

    @Override
    public void beforeTestSuite(Stage stage, String testCase, Instant startTime, Settings settings) {
        sinks.forEach(sink -> sink.beforeTestSuite(stage, testCase, startTime, settings));
    }

    @Override
    public void timeRecorded(Context context, Record record) {
        sinks.forEach(sink -> sink.timeRecorded(context, record));
    }

    @Override
    public void afterTestCase(Context context, long elapsed, @Nullable Throwable throwable) {
        sinks.forEach(sink -> sink.afterTestCase(context, elapsed, throwable));
    }

    @Override
    public void afterTestSuite(Stage stage, String testCase, Instant startTime, Settings settings) {
        sinks.forEach(sink -> sink.afterTestSuite(stage, testCase, startTime, settings));
    }

    @Override
    public void shutDown() {
        sinks.forEach(Sink::shutDown);
    }
}
