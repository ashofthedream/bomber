package ashes.of.bomber.sink.histo;

import org.HdrHistogram.ConcurrentHistogram;
import org.HdrHistogram.Histogram;

import java.util.concurrent.atomic.LongAdder;

public class HistogramAndErrors {
    public final Histogram histogram = new ConcurrentHistogram(2);
    public final LongAdder errors = new LongAdder();

    public void record(boolean success, long elapsed) {
        if (!success)
            errors.increment();

        histogram.recordValue(elapsed);
    }
}
