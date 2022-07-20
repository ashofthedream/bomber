package ashes.of.bomber.events;

import java.time.Instant;

public record TestSuiteFinishedEvent(Instant timestamp, long flightId, String testApp, String testSuite) {
}
