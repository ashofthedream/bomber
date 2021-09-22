package ashes.of.bomber.snapshots;

import javax.annotation.Nullable;

public class TestAppSnapshot {
    private final String name;

    @Nullable
    private final TestSuiteSnapshot current;

    public TestAppSnapshot(String name, @Nullable TestSuiteSnapshot current) {
        this.name = name;
        this.current = current;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public TestSuiteSnapshot getCurrent() {
        return current;
    }
}
