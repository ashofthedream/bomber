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
    public void beforeAll(Stage stage, String testCase, Instant startTime, Settings settings) {
        sinks.forEach(sink -> sink.beforeAll(stage, testCase, startTime, settings));
    }

    @Override
    public void onTimeRecorded(Context context, Record record) {
        sinks.forEach(sink -> sink.onTimeRecorded(context, record));
    }

    @Override
    public void afterEachTest(Context context, long elapsed, @Nullable Throwable throwable) {
        sinks.forEach(sink -> sink.afterEachTest(context, elapsed, throwable));
    }

    @Override
    public void afterAll(Stage stage, String testCase, Instant startTime, Settings settings) {
        sinks.forEach(sink -> sink.afterAll(stage, testCase, startTime, settings));
    }
}
