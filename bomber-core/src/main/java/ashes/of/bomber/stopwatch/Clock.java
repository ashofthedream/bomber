package ashes.of.bomber.stopwatch;

import java.util.function.Consumer;


public class Clock {

    private final long ts = System.nanoTime();
    private final String prefix;
    private final Consumer<Record> timeRecorded;

    public Clock(String prefix, Consumer<Record> timeRecorded) {
        this.prefix = prefix;
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
        return new Stopwatch(prefix + (label.isEmpty() ? "" : "." + label), timeRecorded);
    }
}
