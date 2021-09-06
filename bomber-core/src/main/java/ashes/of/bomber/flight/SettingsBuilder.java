package ashes.of.bomber.flight;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class SettingsBuilder {

    /**
     * This flag indicates that stage may be disabled
     */
    private boolean disabled;

    /**
     * Stage duration time
     */
    private Duration time = Duration.ofMinutes(1);;

    /**
     * Threads count
     */
    private int threadsCount = 1;

    /**
     * Iterations count per each thread
     */
    private long threadIterationsCount = 1_000_000;

    /**
     * Total iterations count
     */
    private long totalIterationsCount = 1_000_000;

    public static SettingsBuilder of(Settings settings) {
        return new SettingsBuilder()
                .setDisabled(settings.isDisabled())
                .setTime(settings.getTime())
                .setThreadsCount(settings.getThreadsCount())
                .setThreadIterationsCount(settings.getThreadIterationsCount())
                .setTotalIterationsCount(settings.getTotalIterationsCount());
    }

    public static Settings disabled() {
        return new Settings(true, Duration.ZERO, 0, 0, 0);
    }

    public SettingsBuilder setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public SettingsBuilder setTime(Duration time) {
        this.time = time;
        return this;
    }

    public SettingsBuilder setTime(long time, TimeUnit unit) {
        return setTime(Duration.ofMillis(unit.toMillis(time)));
    }

    public SettingsBuilder setSeconds(long seconds) {
        return setTime(Duration.ofSeconds(seconds));
    }

    public SettingsBuilder setThreadsCount(int threadsCount) {
        this.threadsCount = threadsCount;
        return this;
    }

    public SettingsBuilder setThreadIterationsCount(long threadIterationsCount) {
        this.threadIterationsCount = threadIterationsCount;
        return this;
    }

    public SettingsBuilder setTotalIterationsCount(long totalIterationsCount) {
        this.totalIterationsCount = totalIterationsCount;
        return this;
    }

    public Settings build() {
        return new Settings(disabled, time, threadsCount, threadIterationsCount, totalIterationsCount);
    }
}
