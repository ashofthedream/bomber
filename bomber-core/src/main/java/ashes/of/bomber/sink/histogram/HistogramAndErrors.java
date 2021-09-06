package ashes.of.bomber.sink.histogram;

import org.HdrHistogram.ConcurrentHistogram;
import org.HdrHistogram.Histogram;

import java.util.concurrent.atomic.LongAdder;

public class HistogramAndErrors {
    private final Histogram histogram = new ConcurrentHistogram(2);
    private final LongAdder errors = new LongAdder();

    public void record(boolean success, long elapsed) {
        if (!success)
            errors.increment();

        histogram.recordValue(elapsed);
    }

    public Histogram getHistogram() {
        return histogram;
    }

    public long getErrorsCount() {
        return errors.sum();
    }
}
