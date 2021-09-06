package ashes.of.bomber.flight;

import javax.annotation.Nullable;

public class TestCasePlan {
    private final String name;

    @Nullable
    private final Settings warmUp;

    @Nullable
    private final Settings settings;


    public TestCasePlan(String name, @Nullable Settings warmUp, @Nullable Settings settings) {
        this.name = name;
        this.warmUp = warmUp;
        this.settings = settings;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public Settings getWarmUp() {
        return warmUp;
    }

    @Nullable
    public Settings getSettings() {
        return settings;
    }
}
