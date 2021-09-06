package ashes.of.bomber.carrier.dto.requests;

import ashes.of.bomber.carrier.dto.TestSuiteDto;

import java.util.List;

public class StartFlightRequest {
    private long flightId;
    private List<TestSuiteDto> testSuites;

    public long getFlightId() {
        return flightId;
    }

    public StartFlightRequest setFlightId(long flightId) {
        this.flightId = flightId;
        return this;
    }

    public List<TestSuiteDto> getTestSuites() {
        return testSuites;
    }

    public StartFlightRequest setTestSuites(List<TestSuiteDto> testSuites) {
        this.testSuites = testSuites;
        return this;
    }
}
