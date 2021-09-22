package ashes.of.bomber.sink.histogram;

import ashes.of.bomber.core.Test;

public interface HistogramPrinter {
    void print(Test test, Measurements measurements);
}
