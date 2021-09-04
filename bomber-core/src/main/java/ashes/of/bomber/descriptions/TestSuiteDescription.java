package ashes.of.bomber.descriptions;

import ashes.of.bomber.flight.Settings;

import java.util.List;

public class TestSuiteDescription {
    private final String name;
    private final List<TestCaseDescription> testCases;
    private final Settings settings;
    private final Settings warmUp;

    public TestSuiteDescription(String name, List<TestCaseDescription> testCases, Settings settings, Settings warmUp) {
        this.name = name;
        this.testCases = testCases;
        this.settings = settings;
        this.warmUp = warmUp;
    }

    public String getName() {
        return name;
    }

    public List<TestCaseDescription> getTestCases() {
        return testCases;
    }

    public Settings getSettings() {
        return settings;
    }

    public Settings getWarmUp() {
        return warmUp;
    }
}
