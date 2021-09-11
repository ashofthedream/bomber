package ashes.of.bomber.sink;

import ashes.of.bomber.events.TestAppFinishedEvent;
import ashes.of.bomber.events.TestAppStartedEvent;
import ashes.of.bomber.events.TestCaseAfterEachEvent;
import ashes.of.bomber.events.TestCaseBeforeEachEvent;
import ashes.of.bomber.events.TestCaseFinishedEvent;
import ashes.of.bomber.events.TestCaseStartedEvent;
import ashes.of.bomber.events.TestSuiteFinishedEvent;
import ashes.of.bomber.events.TestSuiteStartedEvent;
import ashes.of.bomber.threads.BomberThreadFactory;
import ashes.of.bomber.tools.Record;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    public void beforeTestApp(TestAppStartedEvent event) {
        ex.execute(() -> sink.beforeTestApp(event));
    }

    @Override
    public void beforeTestSuite(TestSuiteStartedEvent event) {
        ex.execute(() -> sink.beforeTestSuite(event));
    }

    @Override
    public void beforeTestCase(TestCaseStartedEvent event) {
        ex.execute(() -> sink.beforeTestCase(event));
    }

    @Override
    public void beforeEach(TestCaseBeforeEachEvent event) {
        ex.execute(() -> sink.beforeEach(event));
    }

    @Override
    public void timeRecorded(Record record) {
        ex.execute(() -> sink.timeRecorded(record));
    }

    @Override
    public void afterEach(TestCaseAfterEachEvent event) {
        ex.execute(() -> sink.afterEach(event));
    }

    @Override
    public void afterTestCase(TestCaseFinishedEvent event) {
        ex.execute(() -> sink.afterTestCase(event));
    }

    @Override
    public void afterTestSuite(TestSuiteFinishedEvent event) {
        ex.execute(() -> sink.afterTestSuite(event));
    }

    @Override
    public void afterTestApp(TestAppFinishedEvent event) {
        ex.execute(() -> sink.afterTestApp(event));
    }
}
