package ashes.of.bomber.atc.model;

import ashes.of.bomber.carrier.dto.flight.TestFlightSnapshotDto;
import ashes.of.bomber.carrier.dto.events.HistogramPointDto;

import java.util.List;

public class FlightRecord {
    private String type;
    private long timestamp;

    private String testSuite;
    private String testCase;

    private TestFlightSnapshotDto state;
    private List<HistogramPointDto> histograms;

    public FlightRecord(String type, long timestamp, TestFlightSnapshotDto state) {
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

    public TestFlightSnapshotDto getState() {
        return state;
    }

    public void setState(TestFlightSnapshotDto state) {
        this.state = state;
    }

    public List<HistogramPointDto> getHistograms() {
        return histograms;
    }

    public FlightRecord setHistograms(List<HistogramPointDto> histograms) {
        this.histograms = histograms;
        return this;
    }
}
