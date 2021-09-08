package ashes.of.bomber.carrier.dto;

public class ConfigurationDto {
    private SettingsDto settings;
    private SettingsDto warmUp;

    public SettingsDto getSettings() {
        return settings;
    }

    public ConfigurationDto setSettings(SettingsDto settings) {
        this.settings = settings;
        return this;
    }

    public SettingsDto getWarmUp() {
        return warmUp;
    }

    public ConfigurationDto setWarmUp(SettingsDto warmUp) {
        this.warmUp = warmUp;
        return this;
    }
}
