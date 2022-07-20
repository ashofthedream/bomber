package ashes.of.bomber.snapshots;

public record WorkerSnapshot(String worker, long currentIterationsCount, long remainIterationsCount, long errorsCount,
                             long expectedRecordsCount, long caughtRecordsCount) {
}
