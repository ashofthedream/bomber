package ashes.of.bomber.sink.histogram;

import ashes.of.bomber.core.Test;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.NavigableMap;

public class HistogramTimelineDummyPrinter implements HistogramTimelinePrinter {
    @Override
    public void print(TemporalUnit resolution, Map<Test, NavigableMap<Instant, Measurements>> timeline) {}

    @Override
    public void print(TemporalUnit resolution, NavigableMap<Instant, Measurements> timeline) {}
}
