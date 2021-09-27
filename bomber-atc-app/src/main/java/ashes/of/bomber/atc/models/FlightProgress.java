package ashes.of.bomber.atc.models;

public class FlightProgress {
    private String testApp;
    private String testSuite;
    private String testCase;
    private long timeElapsed;
    private long timeTotal;
    private long currentIterationsCount;
    private long totalIterationsCount;
    private long errorsCount;

    public String getTestApp() {
        return testApp;
    }

    public FlightProgress setTestApp(String testApp) {
        this.testApp = testApp;
        return this;
    }

    public String getTestSuite() {
        return testSuite;
    }

    public FlightProgress setTestSuite(String testSuite) {
        this.testSuite = testSuite;
        return this;
    }

    public String getTestCase() {
        return testCase;
    }

    public FlightProgress setTestCase(String testCase) {
        this.testCase = testCase;
        return this;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public FlightProgress setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
        return this;
    }

    public long getTimeTotal() {
        return timeTotal;
    }

    public FlightProgress setTimeTotal(long timeTotal) {
        this.timeTotal = timeTotal;
        return this;
    }

    public long getCurrentIterationsCount() {
        return currentIterationsCount;
    }

    public FlightProgress setCurrentIterationsCount(long currentIterationsCount) {
        this.currentIterationsCount = currentIterationsCount;
        return this;
    }

    public long getTotalIterationsCount() {
        return totalIterationsCount;
    }

    public FlightProgress setTotalIterationsCount(long totalIterationsCount) {
        this.totalIterationsCount = totalIterationsCount;
        return this;
    }

    public long getErrorsCount() {
        return errorsCount;
    }

    public FlightProgress setErrorsCount(long errorsCount) {
        this.errorsCount = errorsCount;
        return this;
    }
}
