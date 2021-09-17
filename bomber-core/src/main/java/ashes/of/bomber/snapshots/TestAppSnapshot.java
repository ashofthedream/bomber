package ashes.of.bomber.snapshots;

public class TestAppSnapshot {
    private String name;
    private TestSuiteSnapshot current;

    public String getName() {
        return name;
    }

    public TestAppSnapshot setName(String name) {
        this.name = name;
        return this;
    }

    public TestSuiteSnapshot getCurrent() {
        return current;
    }

    public TestAppSnapshot setCurrent(TestSuiteSnapshot current) {
        this.current = current;
        return this;
    }
}
