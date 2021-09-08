package ashes.of.bomber.carrier.dto.flight;

import ashes.of.bomber.carrier.dto.ConfigurationDto;

public class TestCasePlanDto {
    private String name;
    private ConfigurationDto configuration;


    public String getName() {
        return name;
    }

    public TestCasePlanDto setName(String name) {
        this.name = name;
        return this;
    }

    public ConfigurationDto getConfiguration() {
        return configuration;
    }

    public TestCasePlanDto setConfiguration(ConfigurationDto configuration) {
        this.configuration = configuration;
        return this;
    }
}
