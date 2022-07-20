package ashes.of.bomber.events;

import java.time.Instant;

public record TestAppStartedEvent(Instant timestamp, long flightId, String testApp) {
}
