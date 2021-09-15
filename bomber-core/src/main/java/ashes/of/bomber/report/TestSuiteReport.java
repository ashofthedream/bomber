package ashes.of.bomber.report;

import java.util.List;

public class TestSuiteReport {

    private final String name;
    private final List<TestCaseReport> testCases;

    public TestSuiteReport(String name, List<TestCaseReport> testCases) {
        this.name = name;
        this.testCases = testCases;
    }

    public String getName() {
        return name;
    }

    public List<TestCaseReport> getTestCases() {
        return testCases;
    }
}
