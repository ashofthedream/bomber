package ashes.of.bomber.flight;

import ashes.of.bomber.core.Test;

import java.time.Instant;

/**
 * todo rename it, an make it as part of TestCaseBeforeEachEvent and TestCaseAfterEachEvent
 */
public record Iteration(long flightId, long number, String thread, Instant timestamp, Test test) {

}
