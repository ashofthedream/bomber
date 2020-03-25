package ashes.of.bomber.sink.influx;

import ashes.of.bomber.core.Iteration;
import ashes.of.bomber.stopwatch.Record;
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
        this(influxDB, "bomber_stopwatch_records", "bomber_tests");
    }

    @Override
    public void timeRecorded(Iteration it, Record record) {
        String error = Optional.ofNullable(record.getError())
                .map(Throwable::getMessage)
                .orElse("");

        influxDB.write(Point.measurement(lapCollectionName)
                .time(it.getTimestamp().toEpochMilli(), TimeUnit.MILLISECONDS)
                .tag("stage",           it.getStage().name())
                .tag("testCase",        it.getTestSuite())
                .tag("test",            it.getTestCase())
                .tag("thread",          it.getThread())
                .tag("error",           error)
                .tag("label",           record.getLabel())
                .addField("iteration",  it.getNumber())
                .addField("elapsed",    record.getElapsed())
                .build());
    }

    @Override
    public void afterEach(Iteration it, long elapsed, @Nullable Throwable throwable) {
        String error = Optional.ofNullable(throwable).map(Throwable::getMessage).orElse("");
        influxDB.write(Point.measurement(testCollectionName)
                .time(it.getTimestamp().toEpochMilli(), TimeUnit.MILLISECONDS)
                .tag("stage",           it.getStage().name())
                .tag("testCase",        it.getTestSuite())
                .tag("test",            it.getTestCase())
                .tag("thread",          it.getThread())
                .tag("error",           error)
                .addField("iteration",  it.getNumber())
                .addField("elapsed",    elapsed)
                .build());
    }

}
