package ashes.of.bomber.core.stopwatch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


public class Stopwatch {

    private final long init = System.nanoTime();
    private final Map<String, Lap> laps = new ConcurrentHashMap<>();
    private final Consumer<Lap.Record> consumer;

    public Stopwatch(Consumer<Lap.Record> consumer) {
        this.consumer = consumer;
    }

    public long elapsed() {
        return System.nanoTime() - init;
    }

    /**
     * Creates new lap with specified label
     *
     * @param label label for lap
     * @return created lap
     */
    public Lap lap(String label) {
        return laps.computeIfAbsent(label, k -> new Lap(k, consumer));
    }

    /**
     * @return laps by label
     */
    public Map<String, Lap> laps() {
        return laps;
    }
}
