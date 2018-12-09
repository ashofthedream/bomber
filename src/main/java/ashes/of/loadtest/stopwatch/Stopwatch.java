package ashes.of.loadtest.stopwatch;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Stopwatch {

    private final long init = System.nanoTime();
    private final List<Lap> laps = new ArrayList<>();


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
        Lap lap = new Lap(label);
        laps.add(lap);

        return lap;
    }

    /**
     * @return stream of stopped laps
     */
    public Stream<Lap> laps() {
        return laps.stream()
                .filter(Lap::isStopped);
    }

    /**
     * @return list of stopped laps by label
     */
    public Map<String, List<Lap>> lapsByLabel() {
        return laps()
                .collect(Collectors.groupingBy(Lap::getName));
    }
}
