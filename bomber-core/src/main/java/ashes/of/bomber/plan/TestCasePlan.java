package ashes.of.bomber.plan;

import ashes.of.bomber.configuration.Configuration;

import javax.annotation.Nullable;

public class TestCasePlan {
    private final String name;

    @Nullable
    private final Configuration configuration;

    public TestCasePlan(String name, @Nullable Configuration configuration) {
        this.name = name;
        this.configuration = configuration;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public Configuration getConfiguration() {
        return configuration;
    }
}
