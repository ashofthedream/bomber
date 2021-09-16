package ashes.of.bomber.carrier.dto.flight;

public class WorkerSnapshotDto {
    private String name;
    private long iterationsCount;
    private long remainIterationsCount;
    private long errorsCount;

    public String getName() {
        return name;
    }

    public WorkerSnapshotDto setName(String name) {
        this.name = name;
        return this;
    }

    public long getIterationsCount() {
        return iterationsCount;
    }

    public WorkerSnapshotDto setIterationsCount(long iterationsCount) {
        this.iterationsCount = iterationsCount;
        return this;
    }

    public long getRemainIterationsCount() {
        return remainIterationsCount;
    }

    public WorkerSnapshotDto setRemainIterationsCount(long remainIterationsCount) {
        this.remainIterationsCount = remainIterationsCount;
        return this;
    }

    public long getErrorsCount() {
        return errorsCount;
    }

    public WorkerSnapshotDto setErrorsCount(long errorsCount) {
        this.errorsCount = errorsCount;
        return this;
    }
}
