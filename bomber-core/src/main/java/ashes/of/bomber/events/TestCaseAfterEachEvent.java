package ashes.of.bomber.events;

import ashes.of.bomber.core.Test;

import javax.annotation.Nullable;
import java.time.Instant;

public record TestCaseAfterEachEvent(Instant timestamp, long flightId, Test test, String worker, long number, long elapsed, @Nullable Throwable throwable) {
}
