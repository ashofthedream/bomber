package ashes.of.bomber.flight;

import java.time.Instant;
import java.util.List;

public class FlightReport {
    private final FlightPlan plan;
    private final Instant startTime;
    private final Instant finishTime;
    private final List<TestSuiteReport> testSuites;

    public FlightReport(FlightPlan plan, Instant startTime, Instant finishTime, List<TestSuiteReport> testSuites) {
        this.plan = plan;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.testSuites = testSuites;
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

    public List<TestSuiteReport> getTestSuites() {
        return testSuites;
    }
}
