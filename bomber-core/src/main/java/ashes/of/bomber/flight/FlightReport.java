package ashes.of.bomber.flight;

import java.time.Instant;

public class FlightReport {
    private final FlightPlan plan;
    private final Instant startTime;
    private final Instant finishTime;

    public FlightReport(FlightPlan plan, Instant startTime, Instant finishTime) {
        this.plan = plan;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    public FlightPlan getPlan() {
        return plan;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getFinishTime() {
        return finishTime;
    }
}
