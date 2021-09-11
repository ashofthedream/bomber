package ashes.of.bomber.events;

import java.time.Instant;

public class TestAppFinishedEvent {
    private final Instant timestamp;
    private final long flightId;
    private final String testApp;

    public TestAppFinishedEvent(Instant timestamp, long flightId, String testApp) {
        this.timestamp = timestamp;
        this.flightId = flightId;
        this.testApp = testApp;
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
}
