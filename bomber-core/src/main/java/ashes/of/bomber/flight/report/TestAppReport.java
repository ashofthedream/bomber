package ashes.of.bomber.flight.report;

import ashes.of.bomber.flight.plan.TestAppPlan;

import java.time.Instant;
import java.util.List;

public class TestAppReport {
    private final TestAppPlan plan;
    private final String name;
    private final Instant startTime;
    private final Instant finishTime;
    private final List<TestSuiteReport> testSuites;

    public TestAppReport(TestAppPlan plan, String name, Instant startTime, Instant finishTime, List<TestSuiteReport> testSuites) {
        this.plan = plan;
        this.name = name;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.testSuites = testSuites;
    }

    public TestAppPlan getPlan() {
        return plan;
    }

    public String getName() {
        return name;
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
