package ashes.of.bomber.builder;

import ashes.of.bomber.configuration.Builder;
import ashes.of.bomber.configuration.Settings;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class SettingsBuilder implements Builder<Settings> {

    /**
     * Stage duration time
     */
    private Duration time = Duration.ofMinutes(1);

    /**
     * Threads count
     */
    private int threads = 1;

    /**
     * Total iterations count
     */
    private long iterations = 1_000_000_000;

    public static SettingsBuilder of(Settings settings) {
        return new SettingsBuilder()
                .setDuration(settings.duration())
                .setThreads(settings.threads())
                .setIterations(settings.iterations());
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

    public SettingsBuilder setThreads(int threads) {
        this.threads = threads;
        return this;
    }

    public SettingsBuilder setIterations(long iterations) {
        this.iterations = iterations;
        return this;
    }

    public Settings build() {
        return new Settings(time, threads, iterations);
    }
}
