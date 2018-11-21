package ashes.of.loadtest.stopwatch;

import java.util.LinkedHashMap;
import java.util.Map;

public class Stopwatch {

    private final long init = System.nanoTime();
    private final Map<String, Long> labels = new LinkedHashMap<>();


    public void record(String label) {
        labels.put(label, System.nanoTime() - init);
    }

    public long elapsed() {
        return System.nanoTime() - init;
    }

    public Map<String, Long> getLabels() {
        return labels;
    }
}
