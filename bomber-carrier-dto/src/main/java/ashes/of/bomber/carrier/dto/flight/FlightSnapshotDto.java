package ashes.of.bomber.carrier.dto.flight;

import java.util.List;

public class FlightSnapshotDto {
    private SettingsDto settings;
    private String testApp;
    private String testSuite;
    private String testCase;
    private long testSuiteStart;
    private long testCaseStart;
    private long remainTotalIterations;
    private long elapsedTime;
    private long remainTime;
    private long errorsCount;
    private List<WorkerSnapshotDto> workers;


    public SettingsDto getSettings() {
        return settings;
    }

    public FlightSnapshotDto setSettings(SettingsDto settings) {
        this.settings = settings;
        return this;
    }

    public String getTestApp() {
        return testApp;
    }

    public FlightSnapshotDto setTestApp(String testApp) {
        this.testApp = testApp;
        return this;
    }

    public String getTestSuite() {
        return testSuite;
    }

    public FlightSnapshotDto setTestSuite(String testSuite) {
        this.testSuite = testSuite;
        return this;
    }

    public String getTestCase() {
        return testCase;
    }

    public FlightSnapshotDto setTestCase(String testCase) {
        this.testCase = testCase;
        return this;
    }

    public long getTestSuiteStart() {
        return testSuiteStart;
    }

    public FlightSnapshotDto setTestSuiteStart(long testSuiteStart) {
        this.testSuiteStart = testSuiteStart;
        return this;
    }

    public long getTestCaseStart() {
        return testCaseStart;
    }

    public FlightSnapshotDto setTestCaseStart(long testCaseStart) {
        this.testCaseStart = testCaseStart;
        return this;
    }

    public long getRemainTotalIterations() {
        return remainTotalIterations;
    }

    public FlightSnapshotDto setRemainTotalIterations(long remainTotalIterations) {
        this.remainTotalIterations = remainTotalIterations;
        return this;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public FlightSnapshotDto setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
        return this;
    }

    public long getRemainTime() {
        return remainTime;
    }

    public FlightSnapshotDto setRemainTime(long remainTime) {
        this.remainTime = remainTime;
        return this;
    }

    public long getErrorsCount() {
        return errorsCount;
    }

    public FlightSnapshotDto setErrorsCount(long errorsCount) {
        this.errorsCount = errorsCount;
        return this;
    }

    public List<WorkerSnapshotDto> getWorkers() {
        return workers;
    }

    public FlightSnapshotDto setWorkers(List<WorkerSnapshotDto> workers) {
        this.workers = workers;
        return this;
    }
}
