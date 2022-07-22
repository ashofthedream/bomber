package ashes.of.bomber.runner;

import ashes.of.bomber.core.TestSuite;
import ashes.of.bomber.flight.plan.TestSuitePlan;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestSuiteState {
    private final TestAppState parent;
    private final TestSuitePlan plan;
    private final TestSuite<Object> testSuite;
    private final Instant startTime = Instant.now();
    private volatile Instant finishTime;

    private List<TestCaseState> testCases = new CopyOnWriteArrayList<>();
    private volatile TestCaseState current;

    public TestSuiteState(TestAppState parent, TestSuitePlan plan, TestSuite<Object> testSuite) {
        this.parent = parent;
        this.plan = plan;
        this.testSuite = testSuite;
    }

    public TestAppState getAppState() {
        return parent;
    }

    public TestSuitePlan getPlan() {
        return plan;
    }

    public TestSuite<Object> getTestSuite() {
        return testSuite;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getFinishTime() {
        return finishTime;
    }

    public long getFlightId() {
        return parent.getFlightId();
    }

    public TestCaseState getCurrentCase() {
        return current;
    }

    public void finish() {
        this.finishTime = Instant.now();
    }

    public void attach(TestCaseState state) {
        testCases.add(state);
        current = state;
    }
}
