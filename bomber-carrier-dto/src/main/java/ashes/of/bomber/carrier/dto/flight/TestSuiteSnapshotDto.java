package ashes.of.bomber.carrier.dto.flight;

public class TestSuiteSnapshotDto {
    private String name;
    private TestCaseSnapshotDto current;

    public String getName() {
        return name;
    }

    public TestSuiteSnapshotDto setName(String name) {
        this.name = name;
        return this;
    }

    public TestCaseSnapshotDto getCurrent() {
        return current;
    }

    public TestSuiteSnapshotDto setCurrent(TestCaseSnapshotDto current) {
        this.current = current;
        return this;
    }
}
