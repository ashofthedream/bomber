package ashes.of.bomber.core;

import java.time.Instant;


public class Iteration {

    private final long number;
    private final Stage stage;
    private final String testSuite;
    private final String testCase;
    private final String thread;
    private final Instant timestamp;

    public Iteration(long number, Stage stage, String thread, Instant timestamp, String testSuite, String testCase) {
        this.number = number;
        this.stage = stage;
        this.testSuite = testSuite;
        this.testCase = testCase;
        this.thread = thread;
        this.timestamp = timestamp;
    }

    public long getNumber() {
        return number;
    }

    public Stage getStage() {
        return stage;
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
                ", stage=" + stage +
                ", testSuite='" + testSuite + '\'' +
                ", testCase='" + testCase + '\'' +
                ", thread='" + thread + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
