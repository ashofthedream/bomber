package ashes.of.bomber.events;

import ashes.of.bomber.configuration.Settings;
import ashes.of.bomber.core.Test;

import java.time.Instant;

public record TestCaseStartedEvent(Instant timestamp, long flightId, Test test, Settings settings) {
}
