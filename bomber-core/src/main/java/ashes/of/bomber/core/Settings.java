package ashes.of.bomber.core;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


/**
 * Runner settings
 */
public class Settings {

    /**
     * This flag indicates that stage may be disabled
     */
    private boolean disabled;

    /**
     * Stage duration time
     */
    private Duration time = Duration.ofMinutes(1);

    /**
     * Threads count
     */
    private int threadsCount = 1;

    /**
     * Iterations count per each thread
     */
    private long threadIterationsCount = 1_000_000_000;

    /**
     * Total iterations count
     */
    private long totalIterationsCount = 1_000_000_000;


    public Settings(Settings settings) {
        this.disabled = settings.isDisabled();
        this.time = settings.getTime();
        this.threadsCount = settings.getThreadsCount();
        this.threadIterationsCount = settings.getThreadIterationsCount();
        this.totalIterationsCount = settings.getTotalIterationsCount();
    }

    public Settings() {
    }


    public Settings disabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public Settings disabled() {
        disabled(true);
        return this;
    }


    public Settings time(long time, TimeUnit unit) {
        return duration(Duration.ofMillis(unit.toMillis(time)));
    }

    public Settings seconds(long seconds) {
        return duration(Duration.ofSeconds(seconds));
    }

    public Settings duration(Duration time) {
        this.time = time;
        return this;
    }


    public Settings threadCount(int threads) {
        this.threadsCount = threads;
        return this;
    }


    public Settings threadIterations(long count) {
        this.threadIterationsCount = count;
        return this;
    }

    public Settings totalIterations(long count) {
        this.totalIterationsCount = count;
        return this;
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
