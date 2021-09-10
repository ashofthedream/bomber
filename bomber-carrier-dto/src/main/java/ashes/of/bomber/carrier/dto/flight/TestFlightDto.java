package ashes.of.bomber.carrier.dto.flight;

import java.util.List;

public class TestFlightDto {
    private long id;
    private List<TestSuiteDto> testSuites;

    public long getId() {
        return id;
    }

    public TestFlightDto setId(long id) {
        this.id = id;
        return this;
    }

    public List<TestSuiteDto> getTestSuites() {
        return testSuites;
    }

    public TestFlightDto setTestSuites(List<TestSuiteDto> testSuites) {
        this.testSuites = testSuites;
        return this;
    }
}
