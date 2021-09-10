package ashes.of.bomber.descriptions;

import ashes.of.bomber.configuration.Settings;

public class ConfigurationDescription {
    private final Settings warmUp;
    private final Settings settings;

    public ConfigurationDescription(Settings warmUp, Settings settings) {
        this.settings = settings;
        this.warmUp = warmUp;
    }

    public Settings getSettings() {
        return settings;
    }

    public Settings getWarmUp() {
        return warmUp;
    }
}
