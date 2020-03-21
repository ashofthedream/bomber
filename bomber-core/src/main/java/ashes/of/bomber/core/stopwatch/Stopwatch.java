package ashes.of.bomber.core.stopwatch;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class Stopwatch {

    private final long ts = System.nanoTime();

    private final String label;
    private final Consumer<Record> timeRecorded;

    public Stopwatch(String label, Consumer<Record> timeRecorded) {
        this.label = label;
        this.timeRecorded = timeRecorded;
    }


    public Record success() {
        Record record = new Record(label, this.ts, System.nanoTime() - this.ts, true, null);
        timeRecorded.accept(record);
        return record;
    }

    public Record fail(@Nullable Throwable th) {
        Record record = new Record(label, this.ts, System.nanoTime() - this.ts, false, th);
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
