package ashes.of.loadtest.runner;

import java.time.Instant;


public class TestContext {

    private final TestCaseContext testCase;
    private final String name;
    private final Instant startTime;

    public TestContext(TestCaseContext testCase, String name, Instant startTime) {
        this.testCase = testCase;
        this.name = name;
        this.startTime = startTime;
    }

    public long getInvocationNumber() {
        return testCase.getInvocationNumber();
    }

    public TestCaseContext getTestCase() {
        return testCase;
    }

    public String getName() {
        return name;
    }

    public Instant getStartTime() {
        return startTime;
    }
}
