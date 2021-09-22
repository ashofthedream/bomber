package ashes.of.bomber.snapshots;

import ashes.of.bomber.flight.plan.TestFlightPlan;

import javax.annotation.Nullable;
import java.util.List;

public class TestFlightSnapshot {
    private final TestFlightPlan plan;

    @Nullable
    private final TestAppSnapshot current;

    private final List<WorkerSnapshot> workers;

    public TestFlightSnapshot(TestFlightPlan plan, @Nullable TestAppSnapshot current, List<WorkerSnapshot> workers) {
        this.plan = plan;
        this.current = current;
        this.workers = workers;
    }

    public TestFlightPlan getPlan() {
        return plan;
    }

    @Nullable
    public TestAppSnapshot getCurrent() {
        return current;
    }

    public List<WorkerSnapshot> getWorkers() {
        return workers;
    }
}
