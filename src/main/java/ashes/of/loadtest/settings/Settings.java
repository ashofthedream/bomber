package ashes.of.loadtest.settings;

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
     * Thread count
     */
    private int threads = 1;

    /**
     * Invocation count per each thread
     */
    private long threadInvocations = Long.MAX_VALUE;

    /**
     * Overall invocation count
     */
    private long totalInvocations = Long.MAX_VALUE;


    public Settings(Settings settings) {
        this.disabled = settings.isDisabled();
        this.time = settings.getTime();
        this.threads = settings.getThreads();
        this.threadInvocations = settings.getThreadInvocations();
        this.totalInvocations = settings.getTotalInvocations();
    }

    public Settings() {
    }


    public Settings setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public Settings disabled() {
        setDisabled(true);
        return this;
    }


    public Settings time(long time, TimeUnit unit) {
        return time(unit.toMillis(time));
    }

    public Settings time(long ms) {
        return time(Duration.ofMillis(ms));
    }

    public Settings time(Duration testTime) {
        this.time = testTime;
        return this;
    }


    public Settings threads(int threads) {
        this.threads = threads;
        return this;
    }


    public Settings threadIterationCount(long threadIterationCount) {
        this.threadInvocations = threadIterationCount;
        return this;
    }


    public Settings totalIterationCount(long totalIterationCount) {
        this.totalInvocations = totalIterationCount;
        return this;
    }


    public boolean isDisabled() {
        return disabled;
    }

    public Duration getTime() {
        return time;
    }

    public int getThreads() {
        return threads;
    }

    public long getThreadInvocations() {
        return threadInvocations;
    }

    public long getTotalInvocations() {
        return totalInvocations;
    }
}
