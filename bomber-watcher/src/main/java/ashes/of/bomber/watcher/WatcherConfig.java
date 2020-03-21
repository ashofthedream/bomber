package ashes.of.bomber.watcher;

import java.util.concurrent.TimeUnit;

public class WatcherConfig {

    private final long period;
    private final TimeUnit timeUnit;
    private final Watcher watcher;

    public WatcherConfig(long period, TimeUnit timeUnit, Watcher watcher) {
        this.period = period;
        this.timeUnit = timeUnit;
        this.watcher = watcher;
    }

    public long getPeriod() {
        return period;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public Watcher getWatcher() {
        return watcher;
    }
}
