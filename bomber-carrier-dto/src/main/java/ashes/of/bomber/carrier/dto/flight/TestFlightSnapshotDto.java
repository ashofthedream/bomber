package ashes.of.bomber.carrier.dto.flight;

import java.util.List;

public class TestFlightSnapshotDto {
    private TestFlightDto plan;
    private TestAppSnapshotDto current;
    private List<WorkerSnapshotDto> workers;

    public TestFlightDto getPlan() {
        return plan;
    }

    public TestFlightSnapshotDto setPlan(TestFlightDto plan) {
        this.plan = plan;
        return this;
    }

    public TestAppSnapshotDto getCurrent() {
        return current;
    }

    public TestFlightSnapshotDto setCurrent(TestAppSnapshotDto current) {
        this.current = current;
        return this;
    }

    public List<WorkerSnapshotDto> getWorkers() {
        return workers;
    }

    public TestFlightSnapshotDto setWorkers(List<WorkerSnapshotDto> workers) {
        this.workers = workers;
        return this;
    }
}
