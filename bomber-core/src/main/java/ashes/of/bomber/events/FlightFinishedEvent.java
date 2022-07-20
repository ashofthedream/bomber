package ashes.of.bomber.events;

import java.time.Instant;

public record FlightFinishedEvent(Instant timestamp, long flightId) {
}
