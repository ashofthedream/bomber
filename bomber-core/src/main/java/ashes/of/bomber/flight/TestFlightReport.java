package ashes.of.bomber.flight;

import java.time.Instant;
import java.util.List;

public class TestFlightReport {
    private final TestFlightPlan plan;
    private final Instant startTime;
    private final Instant finishTime;
    private final List<TestSuiteReport> testSuites;

    public TestFlightReport(TestFlightPlan plan, Instant startTime, Instant finishTime, List<TestSuiteReport> testSuites) {
        this.plan = plan;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.testSuites = testSuites;
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

    public List<TestSuiteReport> getTestSuites() {
        return testSuites;
    }
}
