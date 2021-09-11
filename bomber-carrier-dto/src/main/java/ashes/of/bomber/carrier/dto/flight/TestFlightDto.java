package ashes.of.bomber.carrier.dto.flight;

import java.util.List;

public class TestFlightDto {
    private Long id;
    private List<TestAppDto> testApps;

    public Long getId() {
        return id;
    }

    public TestFlightDto setId(Long id) {
        this.id = id;
        return this;
    }

    public List<TestAppDto> getTestApps() {
        return testApps;
    }

    public TestFlightDto setTestApps(List<TestAppDto> testApps) {
        this.testApps = testApps;
        return this;
    }

    @Override
    public String toString() {
        return "TestFlightDto{" +
                "id=" + id +
                ", testApps=" + testApps +
                '}';
    }
}
