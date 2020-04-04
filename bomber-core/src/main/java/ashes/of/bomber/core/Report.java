package ashes.of.bomber.core;

import java.time.Instant;

public class Report {
    private final Plan plan;
    private final Instant startTime;
    private final Instant finishTime;

    public Report(Plan plan, Instant startTime, Instant finishTime) {
        this.plan = plan;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    public Plan getPlan() {
        return plan;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getFinishTime() {
        return finishTime;
    }
}
