package ashes.of.bomber.configuration;

import java.time.Duration;


/**
 * Runner settings
 */
public class Settings {

    /**
     * Stage duration time
     */
    private final Duration duration;

    /**
     * Threads count
     */
    private final int threadsCount;

    /**
     * Iterations count per each thread
     */
    private final long threadIterationsCount;

    /**
     * Total iterations count
     */
    private final long totalIterationsCount;

    public Settings(Duration duration, int threadsCount, long threadIterationsCount, long totalIterationsCount) {
        this.duration = duration;
        this.threadsCount = threadsCount;
        this.threadIterationsCount = threadIterationsCount;
        this.totalIterationsCount = totalIterationsCount;
    }

    public Duration getDuration() {
        return duration;
    }

    public int getThreadsCount() {
        return threadsCount;
    }

    public long getThreadIterationsCount() {
        return threadIterationsCount;
    }

    public long getTotalIterationsCount() {
        return totalIterationsCount;
    }

    @Override
    public String toString() {
        return "Settings{" +
                ", duration=" + duration +
                ", threadsCount=" + threadsCount +
                ", threadIterationsCount=" + threadIterationsCount +
                ", totalIterationsCount=" + totalIterationsCount +
                '}';
    }
}
