package ashes.of.bomber.carrier.dto.flight;

import java.util.List;

public class TestSuitePlanDto {
    private String name;
    private List<TestCasePlanDto> testCases;

    public String getName() {
        return name;
    }

    public TestSuitePlanDto setName(String name) {
        this.name = name;
        return this;
    }

    public List<TestCasePlanDto> getTestCases() {
        return testCases;
    }

    public TestSuitePlanDto setTestCases(List<TestCasePlanDto> testCases) {
        this.testCases = testCases;
        return this;
    }
}
