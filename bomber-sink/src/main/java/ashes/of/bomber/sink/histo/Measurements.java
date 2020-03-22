package ashes.of.bomber.sink.histo;

import ashes.of.bomber.core.stopwatch.Record;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Measurements {
    public final Instant timestamp;
    public final Map<String, HistogramAndErrors> data = new ConcurrentHashMap<>();

    public Measurements(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void add(Record record) {
        data.computeIfAbsent(record.getLabel(), label -> new HistogramAndErrors())
                .record(record.isSuccess(), record.getElapsed());
    }
}
