package ashes.of.bomber.sink;

import ashes.of.bomber.flight.Iteration;
import ashes.of.bomber.flight.Settings;
import ashes.of.bomber.flight.Stage;
import ashes.of.bomber.threads.BomberThreadFactory;
import ashes.of.bomber.tools.Record;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class AsyncSink implements Sink {
    private static final Logger log = LogManager.getLogger();

    private static final Executor defaultExecutor = Executors.newFixedThreadPool(1, BomberThreadFactory.asyncSink());

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
    public void startUp(Instant timestamp) {
        ex.execute(() -> sink.startUp(timestamp));
    }

    @Override
    public void beforeTestSuite(Instant timestamp, String testSuite) {
        ex.execute(() -> sink.beforeTestSuite(timestamp, testSuite));
    }

    @Override
    public void beforeTestCase(Instant timestamp, Stage stage, String testSuite, String testCase, Settings settings) {
        ex.execute(() -> sink.beforeTestCase(timestamp, stage, testSuite, testCase, settings));
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
    public void afterTestSuite(String testSuite) {
        ex.execute(() -> sink.afterTestSuite(testSuite));
    }

    @Override
    public void shutDown(Instant timestamp) {
        ex.execute(() -> sink.shutDown(timestamp));
    }
}
