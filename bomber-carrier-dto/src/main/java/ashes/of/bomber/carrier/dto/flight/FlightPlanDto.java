package ashes.of.bomber.carrier.dto.flight;

import java.util.List;

public class FlightPlanDto {
    private long id;
    private List<TestSuitePlanDto> testSuites;

    public long getId() {
        return id;
    }

    public FlightPlanDto setId(long id) {
        this.id = id;
        return this;
    }

    public List<TestSuitePlanDto> getTestSuites() {
        return testSuites;
    }

    public FlightPlanDto setTestSuites(List<TestSuitePlanDto> testSuites) {
        this.testSuites = testSuites;
        return this;
    }
}
