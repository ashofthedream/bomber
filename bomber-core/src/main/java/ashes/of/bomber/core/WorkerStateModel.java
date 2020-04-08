package ashes.of.bomber.core;

public class WorkerStateModel {
    private final String worker;

    private final long currentIterationCount;
    private final long remainIterationsCount;
    private final long errorsCount;
    private final long expectedRecordsCount;
    private final long caughtRecordsCount;

    public WorkerStateModel(String worker, long currentIterationCount, long remainIterationsCount, long errorsCount, long expectedRecordsCount, long caughtRecordsCount) {
        this.worker = worker;
        this.currentIterationCount = currentIterationCount;
        this.remainIterationsCount = remainIterationsCount;
        this.errorsCount = errorsCount;
        this.expectedRecordsCount = expectedRecordsCount;
        this.caughtRecordsCount = caughtRecordsCount;
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

    public long getExpectedRecordsCount() {
        return expectedRecordsCount;
    }

    public long getCaughtRecordsCount() {
        return caughtRecordsCount;
    }
}
