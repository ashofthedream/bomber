package ashes.of.loadtest.stopwatch;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Lap {

    private final long init = System.nanoTime();

    private final String label;
    private final List<Long> records = new CopyOnWriteArrayList<>();

    public Lap(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * @return records
     */
    public List<Long> records() {
        return records;
    }

    /**
     * Stops this lap
     */
    public long elapsed() {
        long elapsed = System.nanoTime() - init;
        records.add(elapsed);

        return elapsed;
    }

    @Override
    public String toString() {
        List<Double> millis = records.stream()
                .mapToDouble(elapsed -> elapsed / 1_000_000.)
                .boxed()
                .collect(Collectors.toList());

        return "Lap{" + label + ": " + millis + "}";
    }
}
