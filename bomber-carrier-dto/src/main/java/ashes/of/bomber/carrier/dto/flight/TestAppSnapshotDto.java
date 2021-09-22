package ashes.of.bomber.carrier.dto.flight;

public class TestAppSnapshotDto {
    private String name;
    private TestSuiteSnapshotDto current;

    public String getName() {
        return name;
    }

    public TestAppSnapshotDto setName(String name) {
        this.name = name;
        return this;
    }

    public TestSuiteSnapshotDto getCurrent() {
        return current;
    }

    public TestAppSnapshotDto setCurrent(TestSuiteSnapshotDto current) {
        this.current = current;
        return this;
    }
}
