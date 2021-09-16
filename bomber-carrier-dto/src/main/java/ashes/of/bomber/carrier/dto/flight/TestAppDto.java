package ashes.of.bomber.carrier.dto.flight;

import java.util.List;

public class TestAppDto {
    private String name;
    private FlightSnapshotDto state;
    private List<TestSuiteDto> testSuites;

    public String getName() {
        return name;
    }

    public TestAppDto setName(String name) {
        this.name = name;
        return this;
    }

    public FlightSnapshotDto getState() {
        return state;
    }

    public TestAppDto setState(FlightSnapshotDto state) {
        this.state = state;
        return this;
    }

    public List<TestSuiteDto> getTestSuites() {
        return testSuites;
    }

    public TestAppDto setTestSuites(List<TestSuiteDto> testSuites) {
        this.testSuites = testSuites;
        return this;
    }
}
