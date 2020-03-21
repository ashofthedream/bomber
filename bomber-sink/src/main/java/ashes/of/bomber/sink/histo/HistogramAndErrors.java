package ashes.of.bomber.sink.histo;

import ashes.of.bomber.core.stopwatch.Record;
import org.HdrHistogram.ConcurrentHistogram;
import org.HdrHistogram.Histogram;

import java.util.concurrent.atomic.LongAdder;

public class HistogramAndErrors {
    public final Histogram histogram = new ConcurrentHistogram(2);
    public final LongAdder errors = new LongAdder();

    public void record(Record record) {
        if (!record.isSuccess())
            errors.increment();

        histogram.recordValue(record.getElapsed());
    }
}
