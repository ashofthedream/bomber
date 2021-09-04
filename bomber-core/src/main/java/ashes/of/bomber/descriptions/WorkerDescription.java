package ashes.of.bomber.descriptions;

public class WorkerDescription {
    private final String worker;

    private final long currentIterationsCount;
    private final long remainIterationsCount;
    private final long errorsCount;
    private final long expectedRecordsCount;
    private final long caughtRecordsCount;

    public WorkerDescription(String worker, long currentIterationsCount, long remainIterationsCount, long errorsCount, long expectedRecordsCount, long caughtRecordsCount) {
        this.worker = worker;
        this.currentIterationsCount = currentIterationsCount;
        this.remainIterationsCount = remainIterationsCount;
        this.errorsCount = errorsCount;
        this.expectedRecordsCount = expectedRecordsCount;
        this.caughtRecordsCount = caughtRecordsCount;
    }

    public String getWorker() {
        return worker;
    }

    public long getCurrentIterationsCount() {
        return currentIterationsCount;
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
