package ashes.of.bomber.flight.report;

import ashes.of.bomber.configuration.Settings;

public class TestCaseReport {

    private final String name;
    private final Settings settings;
    private final long totalIterationsCount;
    private final long totalErrorsCount;
    private final long totalTimeElapsed;

    public TestCaseReport(String name, Settings settings, long totalIterationsCount, long totalErrorsCount, long totalTimeElapsed) {
        this.name = name;
        this.settings = settings;
        this.totalIterationsCount = totalIterationsCount;
        this.totalErrorsCount = totalErrorsCount;
        this.totalTimeElapsed = totalTimeElapsed;
    }

    public String getName() {
        return name;
    }

    public Settings getSettings() {
        return settings;
    }

    public long getTotalIterationsCount() {
        return totalIterationsCount;
    }

    public long getTotalErrorsCount() {
        return totalErrorsCount;
    }

    public long getTotalTimeElapsed() {
        return totalTimeElapsed;
    }
}
