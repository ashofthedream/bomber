package ashes.of.bomber.sink.datadog;

import ashes.of.bomber.events.TestCaseAfterEachEvent;
import ashes.of.bomber.flight.Iteration;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.tools.Record;
import ashes.of.datadog.client.DatadogClient;

import java.util.Optional;


public class DatadogSink implements Sink {

    private final DatadogClient client;

    public DatadogSink(DatadogClient client) {
        this.client = client;
    }

    @Override
    public void timeRecorded(Record record) {
        Iteration it = record.getIteration();
        String error = Optional.ofNullable(record.getError())
                .map(Throwable::getMessage)
                .orElse("");

        client.timer("bomber_stopwatch_records")
                .tag("testApp",     it.getTest().getTestApp())
                .tag("testSuite",   it.getTest().getTestSuite())
                .tag("testCase",    it.getTest().getTestCase())
                .tag("thread",      it.getThread())
                .tag("error",       error)
                .tag("label",       record.getLabel())
                .nanos(record.getElapsed());
    }

    @Override
    public void afterEach(TestCaseAfterEachEvent event) {
        String error = Optional.ofNullable(event.getThrowable())
                .map(Throwable::getMessage)
                .orElse("");

        client.timer("bomber_tests")
                .tag("testApp",     event.getTest().getTestApp())
                .tag("testSuite",   event.getTest().getTestSuite())
                .tag("testCase",    event.getTest().getTestCase())
                .tag("thread",      event.getWorker())
                .tag("error",       error)
                .nanos(event.getElapsed());
    }
}
