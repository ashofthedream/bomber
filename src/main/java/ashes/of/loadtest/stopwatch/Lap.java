package ashes.of.loadtest.stopwatch;

public class Lap {

    private final long init = System.nanoTime();

    private final String name;
    private volatile long stop;

    public Lap(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * @return true if this laps is stopped
     */
    public boolean isStopped() {
        return stop != 0;
    }

    /**
     * Stops this lap
     */
    public void stop() {
        stop = System.nanoTime();
    }

    /**
     * @return elapsed time in nanoseconds
     */
    public long elapsed() {
        if (!isStopped())
            stop();

        return stop - init;
    }

    @Override
    public String toString() {
        return "Lap{" + name + ":" + (elapsed() / 1_000_000.) + "ms}";
    }
}
