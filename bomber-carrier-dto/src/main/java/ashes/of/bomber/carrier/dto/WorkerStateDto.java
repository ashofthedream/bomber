package ashes.of.bomber.carrier.dto;

public class WorkerStateDto {
    private String name;
    private long iterationsCount;
    private long remainIterationsCount;
    private long errorsCount;

    public String getName() {
        return name;
    }

    public WorkerStateDto setName(String name) {
        this.name = name;
        return this;
    }

    public long getIterationsCount() {
        return iterationsCount;
    }

    public WorkerStateDto setIterationsCount(long iterationsCount) {
        this.iterationsCount = iterationsCount;
        return this;
    }

    public long getRemainIterationsCount() {
        return remainIterationsCount;
    }

    public WorkerStateDto setRemainIterationsCount(long remainIterationsCount) {
        this.remainIterationsCount = remainIterationsCount;
        return this;
    }

    public long getErrorsCount() {
        return errorsCount;
    }

    public WorkerStateDto setErrorsCount(long errorsCount) {
        this.errorsCount = errorsCount;
        return this;
    }
}
