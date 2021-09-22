package ashes.of.bomber.flight.report;

import ashes.of.bomber.flight.plan.TestAppPlan;

import java.time.Instant;
import java.util.List;

public class TestAppReport {
    private final TestAppPlan plan;
    private final Instant startTime;
    private final Instant finishTime;
    private final List<TestSuiteReport> testSuites;

    public TestAppReport(TestAppPlan plan, Instant startTime, Instant finishTime, List<TestSuiteReport> testSuites) {
        this.plan = plan;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.testSuites = testSuites;
    }

    public String getName() {
        return plan.getName();
    }

    public TestAppPlan getPlan() {
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
