package ashes.of.bomber.core;

import java.time.Instant;


public class Context {

    private final Stage stage;
    private final String testSuite;
    private final String testCase;
    private final String thread;
    private final long inv;
    private final Instant timestamp;

    public Context(Stage stage, String testSuite, String testCase, String thread, long inv, Instant timestamp) {
        this.stage = stage;
        this.testSuite = testSuite;
        this.testCase = testCase;
        this.thread = thread;
        this.inv = inv;
        this.timestamp = timestamp;
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

    public long getInv() {
        return inv;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String toLogString() {
        String testCase = this.testCase != null ? "." + this.testCase : "";
        return String.format("(%s) %s%s", stage, testSuite, testCase);
    }

    @Override
    public String toString() {
        return "Context{" +
                "stage=" + stage +
                ", testCase='" + testSuite + '\'' +
                ", test='" + testCase + '\'' +
                ", thread='" + thread + '\'' +
                ", inv=" + inv +
                ", timestamp=" + timestamp +
                '}';
    }
}
