package ashes.of.bomber.flight;

import ashes.of.bomber.descriptions.ConfigurationDescription;

import javax.annotation.Nullable;

public class TestCasePlan {
    private final String name;

    @Nullable
    private final ConfigurationDescription configuration;

    public TestCasePlan(String name, @Nullable ConfigurationDescription configuration) {
        this.name = name;
        this.configuration = configuration;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public ConfigurationDescription getConfiguration() {
        return configuration;
    }
}
