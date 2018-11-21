package ashes.of.loadtest.runner;

import ashes.of.loadtest.Stage;

import java.time.Instant;


public class TestCaseContext {
    private final Stage stage;
    private final String name;
    private final String threadName;
    private final long invocationNumber;
    private final Instant startTime;


    public TestCaseContext(Stage stage, String name, String threadName, long invocationNumber, Instant startTime) {
        this.stage = stage;
        this.name = name;
        this.threadName = threadName;
        this.invocationNumber = invocationNumber;
        this.startTime = startTime;
    }

    public Stage getStage() {
        return stage;
    }

    public String getName() {
        return name;
    }

    public String getThreadName() {
        return threadName;
    }

    public long getInvocationNumber() {
        return invocationNumber;
    }

    public Instant getStartTime() {
        return startTime;
    }
}
