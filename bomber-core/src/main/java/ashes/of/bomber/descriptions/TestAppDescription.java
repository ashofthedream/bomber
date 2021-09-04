package ashes.of.bomber.descriptions;

import ashes.of.bomber.flight.FlightPlan;

import java.util.List;

public class TestAppDescription {
    private final String name;
    private final FlightPlan plan;
    private final TestAppStateDescription state;
    private final List<TestSuiteDescription> testSuites;

    public TestAppDescription(String name, FlightPlan plan, TestAppStateDescription state, List<TestSuiteDescription> testSuites) {
        this.name = name;
        this.plan = plan;
        this.state = state;
        this.testSuites = testSuites;
    }

    public String getName() {
        return name;
    }

    public FlightPlan getPlan() {
        return plan;
    }

    public TestAppStateDescription getState() {
        return state;
    }

    public List<TestSuiteDescription> getTestSuites() {
        return testSuites;
    }
}
