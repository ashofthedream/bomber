package ashes.of.bomber.carrier.dto.flight;

import java.util.List;

public class TestSuiteDto {
    private String name;
    private List<TestCaseDto> testCases;

    public String getName() {
        return name;
    }

    public TestSuiteDto setName(String name) {
        this.name = name;
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
