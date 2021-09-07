package ashes.of.bomber.carrier.dto;

import java.util.List;

public class FlightPlanDto {
    private long id;
    private List<TestSuiteDto> testSuites;

    public long getId() {
        return id;
    }

    public FlightPlanDto setId(long id) {
        this.id = id;
        return this;
    }

    public List<TestSuiteDto> getTestSuites() {
        return testSuites;
    }

    public FlightPlanDto setTestSuites(List<TestSuiteDto> testSuites) {
        this.testSuites = testSuites;
        return this;
    }
}
