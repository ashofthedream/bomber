package ashes.of.bomber.flight;

import java.util.List;

public class FlightPlan {
    private final long id;
    private final List<TestSuitePlan> testSuites;

    public FlightPlan(long id, List<TestSuitePlan> testSuites) {
        this.id = id;
        this.testSuites = testSuites;
    }

    public long getId() {
        return id;
    }

    public List<TestSuitePlan> getTestSuites() {
        return testSuites;
    }
}
