package ashes.of.bomber.core;

public class WorkerStateModel {
    private final String worker;

    private long currentIterationCount;
    private long remainIterationsCount;
    private long errorsCount;

    public WorkerStateModel(String worker, long currentIterationCount, long remainIterationsCount, long errorsCount) {
        this.worker = worker;
        this.currentIterationCount = currentIterationCount;
        this.remainIterationsCount = remainIterationsCount;
        this.errorsCount = errorsCount;
    }

    public String getWorker() {
        return worker;
    }

    public long getCurrentIterationCount() {
        return currentIterationCount;
    }

    public long getRemainIterationsCount() {
        return remainIterationsCount;
    }

    public long getErrorsCount() {
        return errorsCount;
    }
}
