package ashes.of.bomber.flight.report;

import ashes.of.bomber.flight.plan.TestAppPlan;

import java.time.Instant;
import java.util.List;

public record TestAppReport(TestAppPlan plan, Instant startTime, Instant finishTime, List<TestSuiteReport> testSuites) {
}
