package ashes.of.bomber.carrier.dto;

import java.util.List;

public class ApplicationStateDto {
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

    public ApplicationStateDto setStage(String stage) {
        this.stage = stage;
        return this;
    }

    public SettingsDto getSettings() {
        return settings;
    }

    public ApplicationStateDto setSettings(SettingsDto settings) {
        this.settings = settings;
        return this;
    }

    public String getTestSuite() {
        return testSuite;
    }

    public ApplicationStateDto setTestSuite(String testSuite) {
        this.testSuite = testSuite;
        return this;
    }

    public String getTestCase() {
        return testCase;
    }

    public ApplicationStateDto setTestCase(String testCase) {
        this.testCase = testCase;
        return this;
    }

    public long getTestSuiteStart() {
        return testSuiteStart;
    }

    public ApplicationStateDto setTestSuiteStart(long testSuiteStart) {
        this.testSuiteStart = testSuiteStart;
        return this;
    }

    public long getTestCaseStart() {
        return testCaseStart;
    }

    public ApplicationStateDto setTestCaseStart(long testCaseStart) {
        this.testCaseStart = testCaseStart;
        return this;
    }

    public long getRemainTotalIterations() {
        return remainTotalIterations;
    }

    public ApplicationStateDto setRemainTotalIterations(long remainTotalIterations) {
        this.remainTotalIterations = remainTotalIterations;
        return this;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public ApplicationStateDto setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
        return this;
    }

    public long getRemainTime() {
        return remainTime;
    }

    public ApplicationStateDto setRemainTime(long remainTime) {
        this.remainTime = remainTime;
        return this;
    }

    public long getErrorsCount() {
        return errorsCount;
    }

    public ApplicationStateDto setErrorsCount(long errorsCount) {
        this.errorsCount = errorsCount;
        return this;
    }

    public List<WorkerStateDto> getWorkers() {
        return workers;
    }

    public ApplicationStateDto setWorkers(List<WorkerStateDto> workers) {
        this.workers = workers;
        return this;
    }
}
