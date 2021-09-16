package ashes.of.bomber.events;

import javax.annotation.Nullable;
import java.time.Instant;

public class TestCaseAfterEachEvent {
    private final Instant timestamp;
    private final long flightId;
    private final String testApp;
    private final String testSuite;
    private final String testCase;
    private final String worker;
    private final long number;
    private final long elapsed;
    @Nullable
    private final Throwable throwable;

    public TestCaseAfterEachEvent(Instant timestamp, long flightId, String testApp, String testSuite, String testCase, String worker, long number, long elapsed, @Nullable Throwable throwable) {
        this.timestamp = timestamp;
        this.flightId = flightId;
        this.testApp = testApp;
        this.testSuite = testSuite;
        this.testCase = testCase;
        this.worker = worker;
        this.number = number;
        this.elapsed = elapsed;
        this.throwable = throwable;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public long getFlightId() {
        return flightId;
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

    public String getWorker() {
        return worker;
    }

    public long getNumber() {
        return number;
    }

    public long getElapsed() {
        return elapsed;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }
}
