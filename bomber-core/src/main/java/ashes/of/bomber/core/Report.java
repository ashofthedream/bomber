package ashes.of.bomber.core;

import java.time.Instant;

public class Report {
    private final Instant startTime;
    private final Instant finishTime;

    public Report(Instant startTime, Instant finishTime) {
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getFinishTime() {
        return finishTime;
    }
}
