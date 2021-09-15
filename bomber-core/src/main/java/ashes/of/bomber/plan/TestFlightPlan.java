package ashes.of.bomber.plan;

import java.util.List;

public class TestFlightPlan {
    private final Long flightId;
    private final List<TestAppPlan> testApps;

    public TestFlightPlan(Long flightId, List<TestAppPlan> testApps) {
        this.flightId = flightId;
        this.testApps = testApps;
    }

    public Long getFlightId() {
        return flightId;
    }

    public List<TestAppPlan> getTestApps() {
        return testApps;
    }
}
