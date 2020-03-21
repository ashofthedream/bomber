package ashes.of.bomber.sink.datadog;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.stopwatch.Record;
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
    public void timeRecorded(Context context, Record record) {
        String error = Optional.ofNullable(record.getError())
                .map(Throwable::getMessage)
                .orElse("");

        client.timer("bomber_stopwatch_records")
                .tag("stage",    context.getStage().name())
                .tag("testCase", context.getTestSuite())
                .tag("test",     context.getTestCase())
                .tag("thread",   context.getThread())
                .tag("error",    error)
                .tag("label",    record.getLabel())
                .nanos(record.getElapsed());
    }

    @Override
    public void afterTestCase(Context context, long elapsed, @Nullable Throwable throwable) {
        String error = Optional.ofNullable(throwable)
                .map(Throwable::getMessage)
                .orElse("");

        client.timer("bomber_tests")
                .tag("stage",    context.getStage().name())
                .tag("testCase", context.getTestSuite())
                .tag("test",     context.getTestCase())
                .tag("thread",   context.getThread())
                .tag("error",    error)
                .nanos(elapsed);
    }
}
