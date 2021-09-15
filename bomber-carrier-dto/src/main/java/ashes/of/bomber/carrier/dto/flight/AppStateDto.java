package ashes.of.bomber.carrier.dto.flight;

import java.util.List;

public class AppStateDto {
    private String stage;
    private SettingsDto settings;
    private String testSuite;
    private String testCase;
    private long testSuiteStart;
    private long testCaseStart;
    private long remainTotalIterations;
    private long elapsedTime;
    private long remainTime;
    private long errorsCount;
    private List<WorkerStateDto> workers;

    public String getStage() {
        return stage;
    }

    public AppStateDto setStage(String stage) {
        this.stage = stage;
        return this;
    }

    public SettingsDto getSettings() {
        return settings;
    }

    public AppStateDto setSettings(SettingsDto settings) {
        this.settings = settings;
        return this;
    }

    public String getTestSuite() {
        return testSuite;
    }

    public AppStateDto setTestSuite(String testSuite) {
        this.testSuite = testSuite;
        return this;
    }

    public String getTestCase() {
        return testCase;
    }

    public AppStateDto setTestCase(String testCase) {
        this.testCase = testCase;
        return this;
    }

    public long getTestSuiteStart() {
        return testSuiteStart;
    }

    public AppStateDto setTestSuiteStart(long testSuiteStart) {
        this.testSuiteStart = testSuiteStart;
        return this;
    }

    public long getTestCaseStart() {
        return testCaseStart;
    }

    public AppStateDto setTestCaseStart(long testCaseStart) {
        this.testCaseStart = testCaseStart;
        return this;
    }

    public long getRemainTotalIterations() {
        return remainTotalIterations;
    }

    public AppStateDto setRemainTotalIterations(long remainTotalIterations) {
        this.remainTotalIterations = remainTotalIterations;
        return this;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public AppStateDto setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
        return this;
    }

    public long getRemainTime() {
        return remainTime;
    }

    public AppStateDto setRemainTime(long remainTime) {
        this.remainTime = remainTime;
        return this;
    }

    public long getErrorsCount() {
        return errorsCount;
    }

    public AppStateDto setErrorsCount(long errorsCount) {
        this.errorsCount = errorsCount;
        return this;
    }

    public List<WorkerStateDto> getWorkers() {
        return workers;
    }

    public AppStateDto setWorkers(List<WorkerStateDto> workers) {
        this.workers = workers;
        return this;
    }
}
