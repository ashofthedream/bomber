package ashes.of.bomber.events;

import ashes.of.bomber.configuration.Settings;
import ashes.of.bomber.flight.Stage;

import java.time.Instant;

public class TestCaseStartedEvent {
    private final Instant timestamp;
    private final long flightId;
    private final String testApp;
    private final String testSuite;
    private final String testCase;
    private final Stage stage;
    private final Settings settings;

    public TestCaseStartedEvent(Instant timestamp, long flightId, String testApp, String testSuite, String testCase, Stage stage, Settings settings) {
        this.timestamp = timestamp;
        this.flightId = flightId;
        this.testApp = testApp;
        this.testSuite = testSuite;
        this.testCase = testCase;
        this.stage = stage;
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

    public Stage getStage() {
        return stage;
    }

    public Settings getSettings() {
        return settings;
    }
}
