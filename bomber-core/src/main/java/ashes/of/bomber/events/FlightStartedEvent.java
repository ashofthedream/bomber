package ashes.of.bomber.events;

import java.time.Instant;

public class FlightStartedEvent {
    private final Instant timestamp;
    private final long flightId;

    public FlightStartedEvent(Instant timestamp, long flightId) {
        this.timestamp = timestamp;
        this.flightId = flightId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public long getFlightId() {
        return flightId;
    }
}
