package ashes.of.bomber.carrier.dto.flight;

public class SettingsDto {
    private long duration;
    private int threads;
    private long iterations;

    public long getDuration() {
        return duration;
    }

    public SettingsDto setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public int getThreads() {
        return threads;
    }

    public SettingsDto setThreads(int threads) {
        this.threads = threads;
        return this;
    }

    public long getIterations() {
        return iterations;
    }

    public SettingsDto setIterations(long iterations) {
        this.iterations = iterations;
        return this;
    }
}
