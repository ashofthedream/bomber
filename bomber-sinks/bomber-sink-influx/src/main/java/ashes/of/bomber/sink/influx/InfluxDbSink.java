package ashes.of.bomber.sink.influx;

import ashes.of.bomber.events.TestCaseAfterEachEvent;
import ashes.of.bomber.flight.Iteration;
import ashes.of.bomber.tools.Record;
import ashes.of.bomber.sink.Sink;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class InfluxDbSink implements Sink {

    private final InfluxDB db;
    private final String callCollectionName;
    private final String testCollectionName;

    public InfluxDbSink(InfluxDB db, String callCollectionName, String testCollectionName) {
        this.db = db;
        this.callCollectionName = callCollectionName;
        this.testCollectionName = testCollectionName;
    }

    public InfluxDbSink(InfluxDB db) {
        this(db, "bomber_calls", "bomber_tests");
    }

    @Override
    public void timeRecorded(Record record) {
        Iteration it = record.iteration();
        String error = Optional.ofNullable(record.error())
                .map(Throwable::getMessage)
                .orElse("");

        db.write(Point.measurement(callCollectionName)
                .time(it.timestamp().toEpochMilli(), TimeUnit.MILLISECONDS)
                .tag("testApp",         it.test().testApp())
                .tag("testSuite",       it.test().testSuite())
                .tag("testCase",        it.test().testCase())
                .tag("thread",          it.thread())
                .tag("error",           error)
                .tag("label",           record.label())
                .addField("iteration",  it.number())
                .addField("elapsed",    record.elapsed())
                .build());
    }

    @Override
    public void afterEach(TestCaseAfterEachEvent event) {
        String error = Optional.ofNullable(event.throwable())
                .map(Throwable::getMessage)
                .orElse("");

        db.write(Point.measurement(testCollectionName)
                .time(event.timestamp().toEpochMilli(), TimeUnit.MILLISECONDS)
                .tag("testApp",         event.test().testApp())
                .tag("testSuite",       event.test().testSuite())
                .tag("testCase",        event.test().testCase())
                .tag("thread",          event.worker())
                .tag("error",           error)
                .addField("iteration",  event.number())
                .addField("elapsed",    event.elapsed())
                .build());
    }
}
