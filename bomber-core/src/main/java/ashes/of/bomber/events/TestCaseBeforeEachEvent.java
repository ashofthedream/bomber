package ashes.of.bomber.events;

import ashes.of.bomber.core.Test;

import java.time.Instant;

public record TestCaseBeforeEachEvent(Instant timestamp, long flightId, Test test) {
}
