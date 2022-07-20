package ashes.of.bomber.events;

import java.time.Instant;

public record TestSuiteStartedEvent(Instant timestamp, long flightId, String testApp, String testSuite) {
}
