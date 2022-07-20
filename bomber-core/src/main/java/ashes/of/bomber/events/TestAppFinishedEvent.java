package ashes.of.bomber.events;

import java.time.Instant;

public record TestAppFinishedEvent(Instant timestamp, long flightId, String testApp) {
}
