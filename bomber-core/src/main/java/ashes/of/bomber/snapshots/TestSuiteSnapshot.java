package ashes.of.bomber.snapshots;

public class TestSuiteSnapshot {
    private String name;
    private TestCaseSnapshot current;

    public String getName() {
        return name;
    }

    public TestSuiteSnapshot setName(String name) {
        this.name = name;
        return this;
    }

    public TestCaseSnapshot getCurrent() {
        return current;
    }

    public TestSuiteSnapshot setCurrent(TestCaseSnapshot current) {
        this.current = current;
        return this;
    }
}
