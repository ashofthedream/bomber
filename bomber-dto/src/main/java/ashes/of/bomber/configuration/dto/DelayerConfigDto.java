package ashes.of.bomber.configuration.dto;

import javax.annotation.Nullable;

public class DelayerConfigDto {
    private boolean shared;

    @Nullable
    private Long min;

    @Nullable
    private Long max;

    public boolean isShared() {
        return shared;
    }

    public DelayerConfigDto setShared(boolean shared) {
        this.shared = shared;
        return this;
    }

    @Nullable
    public Long getMin() {
        return min;
    }

    public DelayerConfigDto setMin(@Nullable Long min) {
        this.min = min;
        return this;
    }

    @Nullable
    public Long getMax() {
        return max;
    }

    public DelayerConfigDto setMax(@Nullable Long max) {
        this.max = max;
        return this;
    }
}
