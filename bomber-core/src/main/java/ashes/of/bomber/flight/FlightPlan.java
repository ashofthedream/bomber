package ashes.of.bomber.flight;

import java.util.List;

public class FlightPlan {
    private final long flightId;
    private final List<TestSuitePlan> testSuites;

    public FlightPlan(long flightId, List<TestSuitePlan> testSuites) {
        this.flightId = flightId;
        this.testSuites = testSuites;
    }

    public long getFlightId() {
        return flightId;
    }

    public List<TestSuitePlan> getTestSuites() {
        return testSuites;
    }
}
