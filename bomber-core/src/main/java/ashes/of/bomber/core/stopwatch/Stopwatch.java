package ashes.of.bomber.core.stopwatch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Stopwatch {

    private final long init = System.nanoTime();
    private final Map<String, Lap> laps = new ConcurrentHashMap<>();


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
        return laps.computeIfAbsent(label, Lap::new);
    }

    /**
     * @return laps by label
     */
    public Map<String, Lap> laps() {
        return laps;
    }
}
