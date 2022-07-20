package ashes.of.bomber.flight.report;

import ashes.of.bomber.flight.plan.TestFlightPlan;

import java.time.Instant;
import java.util.List;

public record TestFlightReport(TestFlightPlan plan, Instant startTime, Instant finishTime, List<TestAppReport> testApps) {
}
