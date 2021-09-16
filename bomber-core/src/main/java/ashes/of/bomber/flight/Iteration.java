package ashes.of.bomber.flight;

import java.time.Instant;

@Deprecated
public class Iteration {
    private final long flightId;
    private final long number;
    private final String testApp;
    private final String testSuite;
    private final String testCase;
    private final String thread;
    private final Instant timestamp;

    public Iteration(long flightId, long number, String thread, Instant timestamp, String testApp, String testSuite, String testCase) {
        this.flightId = flightId;
        this.number = number;
        this.testApp = testApp;
        this.testSuite = testSuite;
        this.testCase = testCase;
        this.thread = thread;
        this.timestamp = timestamp;
    }

    public long getFlightId() {
        return flightId;
    }

    public long getNumber() {
        return number;
    }

    public String getTestApp() {
        return testApp;
    }

    public String getTestSuite() {
        return testSuite;
    }

    public String getTestCase() {
        return testCase;
    }

    public String getThread() {
        return thread;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Iteration{" +
                "number=" + number +
                ", testApp='" + testApp + '\'' +
                ", testSuite='" + testSuite + '\'' +
                ", testCase='" + testCase + '\'' +
                ", thread='" + thread + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
