package ashes.of.bomber.sink.histogram;

import ashes.of.bomber.tools.Record;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Measurements {
    private final MeasurementKey key;
    public final Map<String, HistogramAndErrors> byLabel = new ConcurrentHashMap<>();

    public Measurements(MeasurementKey key) {
        this.key = key;
    }

    public MeasurementKey getKey() {
        return key;
    }

    public Map<String, HistogramAndErrors> getHistograms() {
        return byLabel;
    }

    public void add(Record record) {
        byLabel.computeIfAbsent(record.getLabel(), label -> new HistogramAndErrors())
                .record(record.isSuccess(), record.getElapsed());
    }
}
