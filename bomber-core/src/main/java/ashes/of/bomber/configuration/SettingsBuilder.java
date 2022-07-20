package ashes.of.bomber.configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class SettingsBuilder {

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

    public static SettingsBuilder of(Settings settings) {
        return new SettingsBuilder()
                .setDuration(settings.duration())
                .setThreadsCount(settings.threadsCount())
                .setThreadIterationsCount(settings.threadIterationsCount())
                .setTotalIterationsCount(settings.totalIterationsCount());
    }

    public SettingsBuilder setDuration(Duration time) {
        this.time = time;
        return this;
    }

    public SettingsBuilder setTime(long time, TimeUnit unit) {
        return setDuration(Duration.ofMillis(unit.toMillis(time)));
    }

    public SettingsBuilder setSeconds(long seconds) {
        return setDuration(Duration.ofSeconds(seconds));
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
        return new Settings(time, threadsCount, threadIterationsCount, totalIterationsCount);
    }
}
