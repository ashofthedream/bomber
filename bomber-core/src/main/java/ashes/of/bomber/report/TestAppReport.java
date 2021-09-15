package ashes.of.bomber.report;

import ashes.of.bomber.plan.TestFlightPlan;

import java.time.Instant;
import java.util.List;

public class TestAppReport {
    private final TestFlightPlan plan;
    private final String name;
    private final Instant startTime;
    private final Instant finishTime;
    private final List<TestSuiteReport> testSuites;

    public TestAppReport(TestFlightPlan plan, String name, Instant startTime, Instant finishTime, List<TestSuiteReport> testSuites) {
        this.plan = plan;
        this.name = name;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.testSuites = testSuites;
    }

    public TestFlightPlan getPlan() {
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
