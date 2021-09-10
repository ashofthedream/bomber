package ashes.of.bomber.carrier.dto.flight;

public class TestCaseDto {
    private String name;
    private ConfigurationDto configuration;

    public String getName() {
        return name;
    }

    public TestCaseDto setName(String name) {
        this.name = name;
        return this;
    }

    public ConfigurationDto getConfiguration() {
        return configuration;
    }

    public TestCaseDto setConfiguration(ConfigurationDto configuration) {
        this.configuration = configuration;
        return this;
    }
}
