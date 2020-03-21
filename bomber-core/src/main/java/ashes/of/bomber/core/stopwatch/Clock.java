package ashes.of.bomber.core.stopwatch;

import java.util.function.Consumer;


public class Clock {

    private final long ts = System.nanoTime();
    private final Consumer<Record> timeRecorded;

    public Clock(Consumer<Record> timeRecorded) {
        this.timeRecorded = timeRecorded;
    }

    public long elapsed() {
        return System.nanoTime() - this.ts;
    }

    /**
     * Creates new stopwatch with specified label
     *
     * @param label label for stopwatch
     * @return created stopwatch
     */
    public Stopwatch stopwatch(String label) {
        return new Stopwatch(label, timeRecorded);
    }
}
