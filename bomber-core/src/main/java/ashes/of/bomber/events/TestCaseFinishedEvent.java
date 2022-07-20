package ashes.of.bomber.events;

import ashes.of.bomber.core.Test;

import java.time.Instant;

public record TestCaseFinishedEvent(Instant timestamp, long flightId, Test test) {
}
