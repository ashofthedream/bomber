package ashes.of.bomber.atc.model;

import ashes.of.bomber.carrier.dto.flight.FlightSnapshotDto;
import ashes.of.bomber.carrier.dto.events.HistogramPointDto;

import java.util.List;

public class FlightRecord {
    private String type;
    private long timestamp;

    private String testSuite;
    private String testCase;

    private FlightSnapshotDto state;
    private List<HistogramPointDto> histograms;

    public FlightRecord(String type, long timestamp, FlightSnapshotDto state) {
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

    public FlightSnapshotDto getState() {
        return state;
    }

    public void setState(FlightSnapshotDto state) {
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
