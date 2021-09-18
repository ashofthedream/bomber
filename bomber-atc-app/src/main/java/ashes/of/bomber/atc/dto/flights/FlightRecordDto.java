package ashes.of.bomber.atc.dto.flights;

import ashes.of.bomber.carrier.dto.flight.TestFlightSnapshotDto;
import ashes.of.bomber.carrier.dto.events.HistogramPointDto;

import java.util.List;

public class FlightRecordDto {
    private long timestamp;
    private String type;
    private String testSuite;
    private String testCase;
    private TestFlightSnapshotDto state;
    private List<HistogramPointDto> histograms;

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

    public TestFlightSnapshotDto getState() {
        return state;
    }

    public FlightRecordDto setState(TestFlightSnapshotDto state) {
        this.state = state;
        return this;
    }

    public List<HistogramPointDto> getHistograms() {
        return histograms;
    }

    public FlightRecordDto setHistograms(List<HistogramPointDto> histograms) {
        this.histograms = histograms;
        return this;
    }
}
