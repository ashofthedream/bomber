package ashes.of.bomber.descriptions;

import java.util.List;

public class TestSuiteDescription {
    private final String name;
    private final List<TestCaseDescription> testCases;

    public TestSuiteDescription(String name, List<TestCaseDescription> testCases) {
        this.name = name;
        this.testCases = testCases;
    }

    public String getName() {
        return name;
    }

    public List<TestCaseDescription> getTestCases() {
        return testCases;
    }
}
