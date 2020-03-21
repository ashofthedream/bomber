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
    public void afterStartUp() {
        ex.execute(sink::afterStartUp);
    }

    @Override
    public void beforeTestSuite(Stage stage, String testCase, Instant startTime, Settings settings) {
        ex.execute(() -> sink.beforeTestSuite(stage, testCase, startTime, settings));
    }

    @Override
    public void timeRecorded(Context context, Record record) {
        ex.execute(() -> sink.timeRecorded(context, record));
    }

    @Override
    public void afterTestCase(Context context, long elapsed, @Nullable Throwable throwable) {
        ex.execute(() -> sink.afterTestCase(context, elapsed, throwable));
    }

    @Override
    public void afterTestSuite(Stage stage, String testCase, Instant startTime, Settings settings) {
        ex.execute(() -> sink.afterTestSuite(stage, testCase, startTime, settings));
    }

    @Override
    public void afterShutdown() {
        ex.execute(sink::afterShutdown);
    }
}
