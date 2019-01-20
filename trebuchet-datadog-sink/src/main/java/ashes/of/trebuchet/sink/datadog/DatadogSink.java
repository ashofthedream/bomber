package ashes.of.trebuchet.sink.datadog;

import ashes.of.datadog.client.DatadogClient;
import ashes.of.trebuchet.runner.Context;
import ashes.of.trebuchet.sink.Sink;
import ashes.of.trebuchet.stopwatch.Lap;
import ashes.of.trebuchet.stopwatch.Stopwatch;

import javax.annotation.Nullable;
import java.util.Optional;


public class DatadogSink implements Sink {

    private final DatadogClient client;

    public DatadogSink(DatadogClient client) {
        this.client = client;
    }


    @Override
    public void afterEach(Context context, long elapsed, Stopwatch stopwatch, @Nullable Throwable throwable) {
        String error = Optional.ofNullable(throwable)
                .map(Throwable::getMessage)
                .orElse("");

        client.timer("trebuchet_tests")
                .tag("stage",           context.getStage().name())
                .tag("testCase",        context.getTestCase())
                .tag("test",            context.getTest())
                .tag("thread",          context.getThread())
                .tag("error",           error)
                .nanos(elapsed);

        stopwatch.laps().forEach((name, lap) -> writeLap(context, name, lap));
    }


    private void writeLap(Context context, String name, Lap lap) {
        lap.records().forEach(record -> {
            client.timer("trebuchet_stopwatch_laps")
                    .tag("stage",           context.getStage().name())
                    .tag("testCase",        context.getTestCase())
                    .tag("test",            context.getTest())
                    .tag("thread",          context.getThread())
                    .tag("lap",             name)
                    .nanos(record);
        });
    }
}
