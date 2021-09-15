package ashes.of.bomber.plan;

import java.util.List;

public class TestAppPlan {

    private final String name;
    private final List<TestSuitePlan> testSuites;

    public TestAppPlan(String name, List<TestSuitePlan> testSuites) {
        this.name = name;
        this.testSuites = testSuites;
    }

    public String getName() {
        return name;
    }

    public List<TestSuitePlan> getTestSuites() {
        return testSuites;
    }
}
