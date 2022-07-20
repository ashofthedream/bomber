package ashes.of.bomber.flight.report;

import ashes.of.bomber.configuration.Settings;

public record TestCaseReport(String name, Settings settings, long iterationsCount, long errorsCount, long totalTimeElapsed) {
}
