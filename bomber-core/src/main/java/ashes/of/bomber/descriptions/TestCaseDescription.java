package ashes.of.bomber.descriptions;

public class TestCaseDescription {
    private final String name;
    private final ConfigurationDescription configuration;

    public TestCaseDescription(String name, ConfigurationDescription configuration) {
        this.name = name;
        this.configuration = configuration;
    }

    public String getName() {
        return name;
    }

    public ConfigurationDescription getConfiguration() {
        return configuration;
    }
}
