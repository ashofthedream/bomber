package ashes.of.bomber.descriptions;

import ashes.of.bomber.flight.TestFlightPlan;

import java.util.List;

public class TestAppDescription {
    private final String name;
    private final TestFlightPlan plan;
    private final TestAppStateDescription state;
    private final List<TestSuiteDescription> testSuites;

    public TestAppDescription(String name, TestFlightPlan plan, TestAppStateDescription state, List<TestSuiteDescription> testSuites) {
        this.name = name;
        this.plan = plan;
        this.state = state;
        this.testSuites = testSuites;
    }

    public String getName() {
        return name;
    }

    public TestFlightPlan getPlan() {
        return plan;
    }

    public TestAppStateDescription getState() {
        return state;
    }

    public List<TestSuiteDescription> getTestSuites() {
        return testSuites;
    }
}
