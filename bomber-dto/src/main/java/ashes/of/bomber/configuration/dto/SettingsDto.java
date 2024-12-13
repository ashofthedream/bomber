package ashes.of.bomber.configuration.dto;

import javax.annotation.Nullable;

public class SettingsDto {
    @Nullable
    private Long time;

    @Nullable
    private Long iterations;

    @Nullable
    private Integer threads;

    public Long getTime() {
        return time;
    }

    public SettingsDto setTime(Long time) {
        this.time = time;
        return this;
    }

    public Integer getThreads() {
        return threads;
    }

    public SettingsDto setThreads(Integer threads) {
        this.threads = threads;
        return this;
    }

    public Long getIterations() {
        return iterations;
    }

    public SettingsDto setIterations(Long iterations) {
        this.iterations = iterations;
        return this;
    }
}
