package ashes.of.bomber.sink.influx;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.stopwatch.Lap;
import ashes.of.bomber.sink.Sink;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class InfluxDbSink implements Sink {

    private final InfluxDB influxDB;
    private final String lapCollectionName;
    private final String testCollectionName;

    public InfluxDbSink(InfluxDB influxDB, String lapCollectionName, String testCollectionName) {
        this.influxDB = influxDB;
        this.lapCollectionName = lapCollectionName;
        this.testCollectionName = testCollectionName;
    }

    public InfluxDbSink(InfluxDB influxDB) {
        this(influxDB, "bomber_stopwatch_laps", "bomber_tests");
    }

    @Override
    public void afterEachLap(Context context, Lap.Record record) {
        String error = Optional.ofNullable(record.getError()).orElse("");
        influxDB.write(Point.measurement(lapCollectionName)
                .time(context.getTimestamp().toEpochMilli(), TimeUnit.MILLISECONDS)
                .tag("stage",           context.getStage().name())
                .tag("testCase",        context.getTestCase())
                .tag("test",            context.getTest())
                .tag("thread",          context.getThread())
                .tag("error",           error)
                .tag("label",           record.getLabel())
                .addField("invocation", context.getInv())
                .addField("elapsed",    record.getElapsed())
                .build());
    }

    @Override
    public void afterEachTest(Context context, long elapsed, @Nullable Throwable throwable) {
        String error = Optional.ofNullable(throwable).map(Throwable::getMessage).orElse("");
        influxDB.write(Point.measurement(testCollectionName)
                .time(context.getTimestamp().toEpochMilli(), TimeUnit.MILLISECONDS)
                .tag("stage",           context.getStage().name())
                .tag("testCase",        context.getTestCase())
                .tag("test",            context.getTest())
                .tag("thread",          context.getThread())
                .tag("error",           error)
                .addField("invocation", context.getInv())
                .addField("elapsed",    elapsed)
                .build());
    }

}
