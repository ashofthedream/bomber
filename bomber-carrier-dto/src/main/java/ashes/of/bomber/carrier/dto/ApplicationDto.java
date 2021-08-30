package ashes.of.bomber.carrier.dto;


import java.util.List;

public class ApplicationDto {
    private String name;
    private ApplicationStateDto state;
    private List<TestSuiteDto> testSuites;

    public String getName() {
        return name;
    }

    public ApplicationDto setName(String name) {
        this.name = name;
        return this;
    }

    public ApplicationStateDto getState() {
        return state;
    }

    public ApplicationDto setState(ApplicationStateDto state) {
        this.state = state;
        return this;
    }

    public List<TestSuiteDto> getTestSuites() {
        return testSuites;
    }

    public ApplicationDto setTestSuites(List<TestSuiteDto> testSuites) {
        this.testSuites = testSuites;
        return this;
    }
}
