package ashes.of.bomber.atc.model;

import ashes.of.bomber.carrier.dto.ApplicationStateDto;

public class FlightRecord {
    private String type;
    private long timestamp;

    private String testSuite;
    private String testCase;

    private ApplicationStateDto state;

    public FlightRecord(String type, long timestamp, ApplicationStateDto state) {
        this.type = type;
        this.timestamp = timestamp;
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTestSuite() {
        return testSuite;
    }

    public void setTestSuite(String testSuite) {
        this.testSuite = testSuite;
    }

    public String getTestCase() {
        return testCase;
    }

    public void setTestCase(String testCase) {
        this.testCase = testCase;
    }

    public ApplicationStateDto getState() {
        return state;
    }

    public void setState(ApplicationStateDto state) {
        this.state = state;
    }
}
