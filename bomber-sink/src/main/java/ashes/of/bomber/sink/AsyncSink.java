package ashes.of.bomber.sink;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;
import ashes.of.bomber.core.stopwatch.Record;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class AsyncSink implements Sink {

    private final Sink sink;
    private final Executor ex;

    public AsyncSink(Sink sink, Executor ex) {
        this.sink = sink;
        this.ex = ex;
    }

    public AsyncSink(Sink sink) {
        this(sink, Executors.newSingleThreadExecutor());
    }

    @Override
    public void beforeAll(Stage stage, String testCase, Instant startTime, Settings settings) {
        ex.execute(() -> sink.beforeAll(stage, testCase, startTime, settings));
    }

    @Override
    public void onTimeRecorded(Context context, Record record) {
        ex.execute(() -> sink.onTimeRecorded(context, record));
    }

    @Override
    public void afterEachTest(Context context, long elapsed, @Nullable Throwable throwable) {
        ex.execute(() -> sink.afterEachTest(context, elapsed, throwable));
    }

    @Override
    public void afterAll(Stage stage, String testCase, Instant startTime, Settings settings) {
        ex.execute(() -> sink.afterAll(stage, testCase, startTime, settings));
    }
}
