package ashes.of.bomber.sink.influx;

import ashes.of.bomber.events.TestCaseAfterEachEvent;
import ashes.of.bomber.flight.Iteration;
import ashes.of.bomber.tools.Record;
import ashes.of.bomber.sink.Sink;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;

import javax.annotation.Nullable;
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
        Iteration it = record.getIteration();
        String error = Optional.ofNullable(record.getError())
                .map(Throwable::getMessage)
                .orElse("");

        db.write(Point.measurement(callCollectionName)
                .time(it.getTimestamp().toEpochMilli(), TimeUnit.MILLISECONDS)
                .tag("testApp",         it.getTest().getTestApp())
                .tag("testSuite",       it.getTest().getTestSuite())
                .tag("testCase",        it.getTest().getTestCase())
                .tag("thread",          it.getThread())
                .tag("error",           error)
                .tag("label",           record.getLabel())
                .addField("iteration",  it.getNumber())
                .addField("elapsed",    record.getElapsed())
                .build());
    }

    @Override
    public void afterEach(TestCaseAfterEachEvent event) {
        String error = Optional.ofNullable(event.getThrowable())
                .map(Throwable::getMessage)
                .orElse("");

        db.write(Point.measurement(testCollectionName)
                .time(event.getTimestamp().toEpochMilli(), TimeUnit.MILLISECONDS)
                .tag("testApp",         event.getTest().getTestApp())
                .tag("testSuite",       event.getTest().getTestSuite())
                .tag("testCase",        event.getTest().getTestCase())
                .tag("thread",          event.getWorker())
                .tag("error",           error)
                .addField("iteration",  event.getNumber())
                .addField("elapsed",    event.getElapsed())
                .build());
    }
}
