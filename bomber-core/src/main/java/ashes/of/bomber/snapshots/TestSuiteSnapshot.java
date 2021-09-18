package ashes.of.bomber.snapshots;

import javax.annotation.Nullable;

public class TestSuiteSnapshot {
    private final String name;
    @Nullable
    private final TestCaseSnapshot current;

    public TestSuiteSnapshot(String name, @Nullable TestCaseSnapshot current) {
        this.name = name;
        this.current = current;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public TestCaseSnapshot getCurrent() {
        return current;
    }

}
