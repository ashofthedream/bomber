package ashes.of.bomber.sink.datadog;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.stopwatch.Lap;
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
    public void afterEachLap(Context context, Lap.Record record) {
        String error = Optional.ofNullable(record.getError())
                .orElse("");

        client.timer("bomber_stopwatch_laps")
                .tag("stage",    context.getStage().name())
                .tag("testCase", context.getTestCase())
                .tag("test",     context.getTest())
                .tag("thread",   context.getThread())
                .tag("error",    error)
                .tag("label",    record.getLabel())
                .nanos(record.getElapsed());
    }

    @Override
    public void afterEachTest(Context context, long elapsed, @Nullable Throwable throwable) {
        String error = Optional.ofNullable(throwable)
                .map(Throwable::getMessage)
                .orElse("");

        client.timer("bomber_tests")
                .tag("stage",    context.getStage().name())
                .tag("testCase", context.getTestCase())
                .tag("test",     context.getTest())
                .tag("thread",   context.getThread())
                .tag("error",    error)
                .nanos(elapsed);
    }
}
