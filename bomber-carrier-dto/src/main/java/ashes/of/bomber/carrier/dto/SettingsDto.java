package ashes.of.bomber.carrier.dto;

public class SettingsDto {
    private boolean disabled;
    private long duration;
    private int threadsCount;
    private long threadIterationsCount;
    private long totalIterationsCount;

    public boolean isDisabled() {
        return disabled;
    }

    public SettingsDto setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public long getDuration() {
        return duration;
    }

    public SettingsDto setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public int getThreadsCount() {
        return threadsCount;
    }

    public SettingsDto setThreadsCount(int threadsCount) {
        this.threadsCount = threadsCount;
        return this;
    }

    public long getThreadIterationsCount() {
        return threadIterationsCount;
    }

    public SettingsDto setThreadIterationsCount(long threadIterationsCount) {
        this.threadIterationsCount = threadIterationsCount;
        return this;
    }

    public long getTotalIterationsCount() {
        return totalIterationsCount;
    }

    public SettingsDto setTotalIterationsCount(long totalIterationsCount) {
        this.totalIterationsCount = totalIterationsCount;
        return this;
    }
}
