package ashes.of.bomber.sink.histogram;

public interface HistogramPrinter {
    void print(MeasurementKey key, Measurements measurements);
}
