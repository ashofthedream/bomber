package ashes.of.bomber.carrier.dto;

import java.util.List;

public class TestSuiteDto {
    private String name;
    private SettingsDto loadTest;
    private SettingsDto warmUp;
    private List<TestCaseDto> testCases;

    public String getName() {
        return name;
    }

    public TestSuiteDto setName(String name) {
        this.name = name;
        return this;
    }

    public SettingsDto getLoadTest() {
        return loadTest;
    }

    public TestSuiteDto setLoadTest(SettingsDto loadTest) {
        this.loadTest = loadTest;
        return this;
    }

    public SettingsDto getWarmUp() {
        return warmUp;
    }

    public TestSuiteDto setWarmUp(SettingsDto warmUp) {
        this.warmUp = warmUp;
        return this;
    }

    public List<TestCaseDto> getTestCases() {
        return testCases;
    }

    public TestSuiteDto setTestCases(List<TestCaseDto> testCases) {
        this.testCases = testCases;
        return this;
    }
}
