package ashes.of.loadtest.runner;

import ashes.of.loadtest.Stage;

import java.time.Instant;


public class Context {

    private final Stage stage;
    private final String testCase;
    private final String test;
    private final String thread;
    private final long inv;
    private final Instant timestamp;

    public Context(Stage stage, String testCase, String test, String thread, long inv, Instant timestamp) {
        this.stage = stage;
        this.testCase = testCase;
        this.test = test;
        this.thread = thread;
        this.inv = inv;
        this.timestamp = timestamp;
    }

    public Stage getStage() {
        return stage;
    }

    public String getTestCase() {
        return testCase;
    }

    public String getTest() {
        return test;
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
}
