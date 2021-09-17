package ashes.of.bomber.snapshots;

import ashes.of.bomber.flight.plan.TestFlightPlan;

import java.util.List;

public class TestFlightSnapshot {
    private TestFlightPlan plan;
    private TestAppSnapshot current;
    private List<WorkerSnapshot> workers;

    public TestFlightPlan getPlan() {
        return plan;
    }

    public TestFlightSnapshot setPlan(TestFlightPlan plan) {
        this.plan = plan;
        return this;
    }

    public TestAppSnapshot getCurrent() {
        return current;
    }

    public TestFlightSnapshot setCurrent(TestAppSnapshot current) {
        this.current = current;
        return this;
    }

    public List<WorkerSnapshot> getWorkers() {
        return workers;
    }

    public TestFlightSnapshot setWorkers(List<WorkerSnapshot> workers) {
        this.workers = workers;
        return this;
    }
}
