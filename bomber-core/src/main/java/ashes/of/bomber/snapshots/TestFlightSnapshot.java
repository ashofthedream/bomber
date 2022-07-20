package ashes.of.bomber.snapshots;

import ashes.of.bomber.flight.plan.TestFlightPlan;

import javax.annotation.Nullable;
import java.util.List;

public record TestFlightSnapshot(TestFlightPlan plan, @Nullable TestAppSnapshot current, List<WorkerSnapshot> workers) {
}
