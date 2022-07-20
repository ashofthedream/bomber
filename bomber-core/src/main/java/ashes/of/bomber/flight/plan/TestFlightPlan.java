package ashes.of.bomber.flight.plan;

import java.util.List;

public record TestFlightPlan(Long flightId, List<TestAppPlan> testApps) {
}
