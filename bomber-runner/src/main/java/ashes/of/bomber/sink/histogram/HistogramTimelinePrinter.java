package ashes.of.bomber.sink.histogram;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.NavigableMap;

public interface HistogramTimelinePrinter {
    void print(TemporalUnit resolution, Map<MeasurementKey, NavigableMap<Instant, Measurements>> timeline);
    void print(TemporalUnit resolution, NavigableMap<Instant, Measurements> timeline);
}
