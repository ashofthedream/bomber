package ashes.of.bomber.runner;

import ashes.of.bomber.core.TestApp;
import ashes.of.bomber.flight.plan.TestAppPlan;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestAppState {
    private final TestFlightState parent;
    private final TestAppPlan plan;
    private final TestApp testApp;
    private final Instant startTime = Instant.now();
    private volatile Instant finishTime;

    private List<TestSuiteState> testSuites = new CopyOnWriteArrayList<>();

    @Nullable
    private volatile TestSuiteState current;

    public TestAppState(TestFlightState parent, TestAppPlan plan, TestApp testApp) {
        this.parent = parent;
        this.plan = plan;
        this.testApp = testApp;
    }

    public TestFlightState getParent() {
        return parent;
    }

    public TestAppPlan getPlan() {
        return plan;
    }

    public TestApp getTestApp() {
        return testApp;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getFinishTime() {
        return finishTime;
    }

    public long getFlightId() {
        return parent.getPlan().getFlightId();
    }

    public TestSuiteState getCurrent() {
        return current;
    }

    public void finish() {
        this.finishTime = Instant.now();
    }

    public void attach(TestSuiteState state) {
        testSuites.add(state);
        current = state;
    }
}
