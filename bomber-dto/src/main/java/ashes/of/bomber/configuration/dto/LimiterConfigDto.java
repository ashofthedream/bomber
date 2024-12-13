package ashes.of.bomber.configuration.dto;

import javax.annotation.Nullable;
import java.time.Duration;

public class LimiterConfigDto {
    private boolean shared;

    @Nullable
    private Integer limit;

    @Nullable
    private Long duration;

    public boolean isShared() {
        return shared;
    }

    public LimiterConfigDto setShared(boolean shared) {
        this.shared = shared;
        return this;
    }

    @Nullable
    public Integer getLimit() {
        return limit;
    }

    public LimiterConfigDto setLimit(@Nullable Integer limit) {
        this.limit = limit;
        return this;
    }

    @Nullable
    public Long getDuration() {
        return duration;
    }

    public LimiterConfigDto setDuration(@Nullable Long duration) {
        this.duration = duration;
        return this;
    }
}
