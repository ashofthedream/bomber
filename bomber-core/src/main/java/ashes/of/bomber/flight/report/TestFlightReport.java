package ashes.of.bomber.flight.report;

import ashes.of.bomber.flight.plan.TestFlightPlan;

import java.time.Instant;
import java.util.List;

public class TestFlightReport {
    private final TestFlightPlan plan;
    private final Instant startTime;
    private final Instant finishTime;
    private final List<TestAppReport> testApps;

    public TestFlightReport(TestFlightPlan plan, Instant startTime, Instant finishTime, List<TestAppReport> testApps) {
        this.plan = plan;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.testApps = testApps;
    }

    public TestFlightPlan getPlan() {
        return plan;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getFinishTime() {
        return finishTime;
    }

    public List<TestAppReport> getTestApps() {
        return testApps;
    }
}
