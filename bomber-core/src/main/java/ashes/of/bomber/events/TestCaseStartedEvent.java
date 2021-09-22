package ashes.of.bomber.events;

import ashes.of.bomber.configuration.Settings;
import ashes.of.bomber.core.Test;

import java.time.Instant;

public class TestCaseStartedEvent {
    private final Instant timestamp;
    private final long flightId;
    private final Test test;
    private final Settings settings;

    public TestCaseStartedEvent(Instant timestamp, long flightId, Test test, Settings settings) {
        this.timestamp = timestamp;
        this.flightId = flightId;
        this.settings = settings;
        this.test = test;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public long getFlightId() {
        return flightId;
    }

    public Test getTest() {
        return test;
    }

    public Settings getSettings() {
        return settings;
    }
}
