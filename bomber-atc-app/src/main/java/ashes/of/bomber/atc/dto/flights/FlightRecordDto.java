package ashes.of.bomber.atc.dto.flights;

import ashes.of.bomber.carrier.dto.ApplicationStateDto;

public class FlightRecordDto {
    private long timestamp;
    private String type;
    private String testSuite;
    private String testCase;
    private ApplicationStateDto state;

    public long getTimestamp() {
        return timestamp;
    }

    public FlightRecordDto setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getType() {
        return type;
    }

    public FlightRecordDto setType(String type) {
        this.type = type;
        return this;
    }

    public String getTestSuite() {
        return testSuite;
    }

    public FlightRecordDto setTestSuite(String testSuite) {
        this.testSuite = testSuite;
        return this;
    }

    public String getTestCase() {
        return testCase;
    }

    public FlightRecordDto setTestCase(String testCase) {
        this.testCase = testCase;
        return this;
    }

    public ApplicationStateDto getState() {
        return state;
    }

    public FlightRecordDto setState(ApplicationStateDto state) {
        this.state = state;
        return this;
    }
}
