package ashes.of.bomber.flight;

import java.util.List;

public class TestSuitePlan {
    private final String name;
    private final List<TestCasePlan> testCases;

    public TestSuitePlan(String name, List<TestCasePlan> testCases) {
        this.name = name;
        this.testCases = testCases;
    }

    public String getName() {
        return name;
    }

    public List<TestCasePlan> getTestCases() {
        return testCases;
    }
}
