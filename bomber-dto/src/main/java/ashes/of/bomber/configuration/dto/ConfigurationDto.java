package ashes.of.bomber.configuration.dto;

public class ConfigurationDto {
    private LimiterConfigDto limiter;
    private DelayerConfigDto delayer;
    private BarrierConfigDto barrier;
    private SettingsDto settings;

    public LimiterConfigDto getLimiter() {
        return limiter;
    }

    public ConfigurationDto setLimiter(LimiterConfigDto limiter) {
        this.limiter = limiter;
        return this;
    }

    public DelayerConfigDto getDelayer() {
        return delayer;
    }

    public ConfigurationDto setDelayer(DelayerConfigDto delayer) {
        this.delayer = delayer;
        return this;
    }

    public BarrierConfigDto getBarrier() {
        return barrier;
    }

    public ConfigurationDto setBarrier(BarrierConfigDto barrier) {
        this.barrier = barrier;
        return this;
    }

    public SettingsDto getSettings() {
        return settings;
    }

    public ConfigurationDto setSettings(SettingsDto settings) {
        this.settings = settings;
        return this;
    }
}
