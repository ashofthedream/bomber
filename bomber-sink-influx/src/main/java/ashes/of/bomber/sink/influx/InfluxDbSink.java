package ashes.of.bomber.sink.influx;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.stopwatch.Lap;
import ashes.of.bomber.core.stopwatch.Stopwatch;
import ashes.of.bomber.sink.Sink;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class InfluxDbSink implements Sink {

    private final InfluxDB influxDB;

    public InfluxDbSink(InfluxDB influxDB) {
        this.influxDB = influxDB;
    }


    @Override
    public void afterEach(Context context, long elapsed, Stopwatch stopwatch, @Nullable Throwable throwable) {
        String error = Optional.ofNullable(throwable).map(Throwable::getMessage).orElse("");
        influxDB.write(Point.measurement("trebuchet_tests")
                .time(context.getTimestamp().toEpochMilli(), TimeUnit.MILLISECONDS)
                .tag("stage",           context.getStage().name())
                .tag("testCase",        context.getTestCase())
                .tag("test",            context.getTest())
                .tag("thread",          context.getThread())
                .tag("error",           error)
                .addField("invocation", context.getInv())
                .addField("elapsed",    elapsed)
                .build());

        stopwatch.laps().forEach((name, lap) -> writeLap(context, name, lap));
    }


    private void writeLap(Context context, String name, Lap lap) {
        lap.records().forEach(record -> {
            influxDB.write(Point.measurement("trebuchet_stopwatch_laps")
                    .time(context.getTimestamp().toEpochMilli(), TimeUnit.MILLISECONDS)
                    .tag("stage",           context.getStage().name())
                    .tag("testCase",        context.getTestCase())
                    .tag("test",            context.getTest())
                    .tag("thread",          context.getThread())
                    .tag("lap",             name)
                    .addField("invocation", context.getInv())
                    .addField("elapsed",    record)
                    .build());
        });
    }
}
