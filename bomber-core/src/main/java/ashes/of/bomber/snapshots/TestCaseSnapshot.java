package ashes.of.bomber.snapshots;

import ashes.of.bomber.configuration.Settings;

public class TestCaseSnapshot {
    private String name;
    private Settings settings;
    private long startTime;
    private long currentIterationsCount;

    public String getName() {
        return name;
    }

    public TestCaseSnapshot setName(String name) {
        this.name = name;
        return this;
    }

    public Settings getSettings() {
        return settings;
    }

    public TestCaseSnapshot setSettings(Settings settings) {
        this.settings = settings;
        return this;
    }

    public long getCurrentIterationsCount() {
        return currentIterationsCount;
    }

    public void setCurrentIterationsCount(long currentIterationsCount) {
        this.currentIterationsCount = currentIterationsCount;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
