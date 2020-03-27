package ashes.of.bomber.core;

import java.util.List;

public class TestSuiteModel {
    private final String name;
    private final List<TestCaseModel> testCases;
    private final Settings settings;
    private final Settings warmUp;

    public TestSuiteModel(String name, List<TestCaseModel> testCases, Settings settings, Settings warmUp) {
        this.name = name;
        this.testCases = testCases;
        this.settings = settings;
        this.warmUp = warmUp;
    }

    public String getName() {
        return name;
    }

    public List<TestCaseModel> getTestCases() {
        return testCases;
    }

    public Settings getSettings() {
        return settings;
    }

    public Settings getWarmUp() {
        return warmUp;
    }
}
