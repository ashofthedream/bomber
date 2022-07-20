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
        Iteration it = record.iteration();
        String error = Optional.ofNullable(record.error())
                .map(Throwable::getMessage)
                .orElse("");

        client.timer("bomber_stopwatch_records")
                .tag("testApp",     it.test().testApp())
                .tag("testSuite",   it.test().testSuite())
                .tag("testCase",    it.test().testCase())
                .tag("thread",      it.thread())
                .tag("error",       error)
                .tag("label",       record.label())
                .nanos(record.elapsed());
    }

    @Override
    public void afterEach(TestCaseAfterEachEvent event) {
        String error = Optional.ofNullable(event.throwable())
                .map(Throwable::getMessage)
                .orElse("");

        client.timer("bomber_tests")
                .tag("testApp",     event.test().testApp())
                .tag("testSuite",   event.test().testSuite())
                .tag("testCase",    event.test().testCase())
                .tag("thread",      event.worker())
                .tag("error",       error)
                .nanos(event.elapsed());
    }
}
