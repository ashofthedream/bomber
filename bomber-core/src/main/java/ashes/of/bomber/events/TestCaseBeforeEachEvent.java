package ashes.of.bomber.events;

import ashes.of.bomber.core.Test;

import java.time.Instant;

public class TestCaseBeforeEachEvent {
    private final Instant timestamp;
    private final long flightId;
    private final Test test;

    public TestCaseBeforeEachEvent(Instant timestamp, long flightId, Test test) {
        this.timestamp = timestamp;
        this.flightId = flightId;
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
}
