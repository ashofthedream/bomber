package ashes.of.bomber.sink;

import ashes.of.bomber.events.TestAppFinishedEvent;
import ashes.of.bomber.events.TestAppStartedEvent;
import ashes.of.bomber.events.TestCaseAfterEachEvent;
import ashes.of.bomber.events.TestCaseBeforeEachEvent;
import ashes.of.bomber.events.TestCaseFinishedEvent;
import ashes.of.bomber.events.TestCaseStartedEvent;
import ashes.of.bomber.events.TestSuiteFinishedEvent;
import ashes.of.bomber.events.TestSuiteStartedEvent;
import ashes.of.bomber.flight.Iteration;
import ashes.of.bomber.tools.Record;

import javax.annotation.Nullable;
import java.util.List;

public class MultiSink implements Sink {

    private final List<Sink> sinks;

    public MultiSink(List<Sink> sinks) {
        this.sinks = sinks;
    }

    @Override
    public void beforeTestApp(TestAppStartedEvent event) {
        sinks.forEach(sink -> sink.beforeTestApp(event));
    }

    @Override
    public void beforeTestSuite(TestSuiteStartedEvent event) {
        sinks.forEach(sink -> sink.beforeTestSuite(event));
    }

    @Override
    public void beforeTestCase(TestCaseStartedEvent event) {
        sinks.forEach(sink -> sink.beforeTestCase(event));
    }

    @Override
    public void beforeEach(TestCaseBeforeEachEvent event) {
        sinks.forEach(sink -> sink.beforeEach(event));
    }

    @Override
    public void timeRecorded(Record record) {
        sinks.forEach(sink -> sink.timeRecorded(record));
    }

    @Override
    public void afterEach(TestCaseAfterEachEvent event) {
        sinks.forEach(sink -> sink.afterEach(event));
    }

    @Override
    public void afterTestCase(TestCaseFinishedEvent event) {
        sinks.forEach(sink -> sink.afterTestCase(event));
    }

    @Override
    public void afterTestSuite(TestSuiteFinishedEvent event) {
        sinks.forEach(sink -> sink.afterTestSuite(event));
    }

    @Override
    public void afterTestApp(TestAppFinishedEvent event) {
        sinks.forEach(sink -> sink.afterTestApp(event));
    }
}
