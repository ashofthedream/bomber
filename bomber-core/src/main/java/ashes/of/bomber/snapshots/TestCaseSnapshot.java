package ashes.of.bomber.snapshots;

import ashes.of.bomber.configuration.Settings;

import javax.annotation.Nullable;
import java.time.Instant;

public class TestCaseSnapshot {
    private final String name;
    private final Settings settings;
    private final Instant startTime;
    @Nullable
    private final Instant finishTime;
    private final long currentIterationsCount;
    private final long errorsCount;

    public TestCaseSnapshot(String name, Settings settings, Instant startTime, @Nullable Instant finishTime, long currentIterationsCount, long errorsCount) {
        this.name = name;
        this.settings = settings;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.currentIterationsCount = currentIterationsCount;
        this.errorsCount = errorsCount;
    }

    public String getName() {
        return name;
    }

    public Settings getSettings() {
        return settings;
    }

    public Instant getStartTime() {
        return startTime;
    }

    @Nullable
    public Instant getFinishTime() {
        return finishTime;
    }

    public long getCurrentIterationsCount() {
        return currentIterationsCount;
    }

    public long getErrorsCount() {
        return errorsCount;
    }
}
