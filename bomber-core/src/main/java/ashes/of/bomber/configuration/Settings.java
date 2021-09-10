package ashes.of.bomber.configuration;

import java.time.Duration;


/**
 * Runner settings
 */
public class Settings {

    /**
     * This flag indicates that stage may be disabled
     */
    private final boolean disabled;

    /**
     * Stage duration time
     */
    private final Duration time;

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

    public Settings(boolean disabled, Duration time, int threadsCount, long threadIterationsCount, long totalIterationsCount) {
        this.disabled = disabled;
        this.time = time;
        this.threadsCount = threadsCount;
        this.threadIterationsCount = threadIterationsCount;
        this.totalIterationsCount = totalIterationsCount;
    }


    public boolean isDisabled() {
        return disabled;
    }

    public Duration getTime() {
        return time;
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
                "disabled=" + disabled +
                ", time=" + time +
                ", threadsCount=" + threadsCount +
                ", threadIterationsCount=" + threadIterationsCount +
                ", totalIterationsCount=" + totalIterationsCount +
                '}';
    }
}
