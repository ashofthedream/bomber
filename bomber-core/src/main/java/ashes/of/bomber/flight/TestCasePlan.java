package ashes.of.bomber.flight;

public class TestCasePlan {
    private final String name;
    private final Settings settings;

    // todo add Setting here

    public TestCasePlan(String name, Settings settings) {
        this.name = name;
        this.settings = settings;
    }

    public String getName() {
        return name;
    }

    public Settings getSettings() {
        return settings;
    }
}
