package ashes.of.bomber.carrier.dto.flight;

public class ConfigurationDto {
    private SettingsDto settings;

    public SettingsDto getSettings() {
        return settings;
    }

    public ConfigurationDto setSettings(SettingsDto settings) {
        this.settings = settings;
        return this;
    }
}
