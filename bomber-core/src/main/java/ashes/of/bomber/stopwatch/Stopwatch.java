package ashes.of.bomber.stopwatch;

import ashes.of.bomber.core.Iteration;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class Stopwatch {

    private final long timestamp = System.nanoTime();

    private final Iteration it;
    private final String label;
    private final Consumer<Record> timeRecorded;

    public Stopwatch(Iteration it, String label, Consumer<Record> timeRecorded) {
        this.it = it;
        this.label = label;
        this.timeRecorded = timeRecorded;
    }


    public long elapsed() {
        return System.nanoTime() - this.timestamp;
    }

    private String label() {
        return it.getTestSuite() + it.getTestCase() + (this.label.isEmpty() ? "" : "." + this.label);
    }


    public Record success() {
        long elapsed = elapsed();
        String label = label();
        Record record = new Record(it, label, this.timestamp, elapsed, true, null);
        timeRecorded.accept(record);
        return record;
    }

    public Record fail(@Nullable Throwable th) {
        long elapsed = elapsed();
        String label = label();
        Record record = new Record(it, label, this.timestamp, elapsed, false, th);
        timeRecorded.accept(record);
        return record;
    }

    public Record fail() {
        return fail(null);
    }


    @Override
    public String toString() {
        return "Stopwatch{" + label + "}";
    }
}
