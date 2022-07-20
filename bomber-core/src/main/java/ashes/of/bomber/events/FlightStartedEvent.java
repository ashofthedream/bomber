package ashes.of.bomber.events;

import java.time.Instant;

public record FlightStartedEvent(Instant timestamp, long flightId) {
}
