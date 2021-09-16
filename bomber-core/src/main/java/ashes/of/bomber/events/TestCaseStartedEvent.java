package ashes.of.bomber.events;

import ashes.of.bomber.configuration.Settings;

import java.time.Instant;

public class TestCaseStartedEvent {
    private final Instant timestamp;
    private final long flightId;
    private final String testApp;
    private final String testSuite;
    private final String testCase;
    private final Settings settings;

    public TestCaseStartedEvent(Instant timestamp, long flightId, String testApp, String testSuite, String testCase, Settings settings) {
        this.timestamp = timestamp;
        this.flightId = flightId;
        this.testApp = testApp;
        this.testSuite = testSuite;
        this.testCase = testCase;
        this.settings = settings;
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

    public Settings getSettings() {
        return settings;
    }
}
