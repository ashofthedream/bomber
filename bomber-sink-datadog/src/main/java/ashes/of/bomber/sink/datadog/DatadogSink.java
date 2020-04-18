package ashes.of.bomber.sink.datadog;

import ashes.of.bomber.core.Iteration;
import ashes.of.bomber.tools.Record;
import ashes.of.bomber.sink.Sink;
import ashes.of.datadog.client.DatadogClient;

import javax.annotation.Nullable;
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
                .tag("stage",    it.getStage().name())
                .tag("testCase", it.getTestSuite())
                .tag("test",     it.getTestCase())
                .tag("thread",   it.getThread())
                .tag("error",    error)
                .tag("label",    record.getLabel())
                .nanos(record.getElapsed());
    }

    @Override
    public void afterEach(Iteration it, long elapsed, @Nullable Throwable throwable) {
        String error = Optional.ofNullable(throwable)
                .map(Throwable::getMessage)
                .orElse("");

        client.timer("bomber_tests")
                .tag("stage",    it.getStage().name())
                .tag("testCase", it.getTestSuite())
                .tag("test",     it.getTestCase())
                .tag("thread",   it.getThread())
                .tag("error",    error)
                .nanos(elapsed);
    }
}
