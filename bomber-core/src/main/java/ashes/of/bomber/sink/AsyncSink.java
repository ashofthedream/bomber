package ashes.of.bomber.sink;

import ashes.of.bomber.core.Iteration;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;
import ashes.of.bomber.stopwatch.Record;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class AsyncSink implements Sink {

    private static final Executor defaultExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r);
        thread.setName("bomber-async-sink");
        thread.setDaemon(true);
        return thread;
    });

    private final Sink sink;
    private final Executor ex;

    public AsyncSink(Sink sink, Executor ex) {
        this.sink = sink;
        this.ex = ex;
    }

    public AsyncSink(Sink sink) {
        this(sink, defaultExecutor);
    }


    @Override
    public void startUp() {
        ex.execute(sink::startUp);
    }

    @Override
    public void beforeTestSuite(Stage stage, String testSuite, Instant timestamp, Settings settings) {
        ex.execute(() -> sink.beforeTestSuite(stage, testSuite, timestamp, settings));
    }

    @Override
    public void beforeTestCase(Stage stage, String testSuite, String testCase, Instant timestamp, Settings settings) {
        ex.execute(() -> sink.beforeTestCase(stage, testSuite, testCase, timestamp, settings));
    }

    @Override
    public void timeRecorded(Record record) {
        ex.execute(() -> sink.timeRecorded(record));
    }

    @Override
    public void afterEach(Iteration it, long elapsed, @Nullable Throwable throwable) {
        ex.execute(() -> sink.afterEach(it, elapsed, throwable));
    }

    @Override
    public void afterTestCase(Stage stage, String testSuite, String testCase) {
        ex.execute(() -> sink.afterTestCase(stage, testSuite, testCase));
    }

    @Override
    public void afterTestSuite(Stage stage, String testSuite) {
        ex.execute(() -> sink.afterTestSuite(stage, testSuite));
    }

    @Override
    public void shutDown() {
        ex.execute(sink::shutDown);
    }
}
