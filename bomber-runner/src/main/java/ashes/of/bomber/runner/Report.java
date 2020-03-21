package ashes.of.bomber.runner;

import java.time.Instant;

public class Report {
    private final Instant startTime;
    private final Instant finishTime;
    private final long errorsCount;

    public Report(Instant startTime, Instant finishTime, long errorsCount) {
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.errorsCount = errorsCount;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getFinishTime() {
        return finishTime;
    }

    public long getErrorsCount() {
        return errorsCount;
    }
}
