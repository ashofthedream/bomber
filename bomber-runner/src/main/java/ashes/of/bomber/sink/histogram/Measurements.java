package ashes.of.bomber.sink.histogram;

import ashes.of.bomber.core.Test;
import ashes.of.bomber.tools.Record;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Measurements {
    private final Test test;
    private final Map<String, HistogramAndErrors> byLabel = new ConcurrentHashMap<>();

    public Measurements(Test test) {
        this.test = test;
    }

    public Test getTest() {
        return test;
    }

    public Map<String, HistogramAndErrors> getHistograms() {
        return byLabel;
    }

    public void add(Record record) {
        byLabel.computeIfAbsent(record.getLabel(), label -> new HistogramAndErrors())
                .record(record.isSuccess(), record.getElapsed());
    }
}
