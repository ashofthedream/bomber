package ashes.of.bomber.events;

import java.time.Instant;

public class TestSuiteFinishedEvent {
    private final Instant timestamp;
    private final long flightId;
    private final String testApp;
    private final String testSuite;

    public TestSuiteFinishedEvent(Instant timestamp, long flightId, String testApp, String testSuite) {
        this.timestamp = timestamp;
        this.flightId = flightId;
        this.testApp = testApp;
        this.testSuite = testSuite;
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
}
