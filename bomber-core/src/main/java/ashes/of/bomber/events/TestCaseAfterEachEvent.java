package ashes.of.bomber.events;

import ashes.of.bomber.core.Test;

import javax.annotation.Nullable;
import java.time.Instant;

public class TestCaseAfterEachEvent {
    private final Instant timestamp;
    private final long flightId;
    private final Test test;
    private final String worker;
    private final long number;
    private final long elapsed;
    @Nullable
    private final Throwable throwable;

    public TestCaseAfterEachEvent(Instant timestamp, long flightId, Test test, String worker, long number, long elapsed, @Nullable Throwable throwable) {
        this.timestamp = timestamp;
        this.flightId = flightId;
        this.test = test;
        this.worker = worker;
        this.number = number;
        this.elapsed = elapsed;
        this.throwable = throwable;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public long getFlightId() {
        return flightId;
    }

    public Test getTest() {
        return test;
    }

    public String getWorker() {
        return worker;
    }

    public long getNumber() {
        return number;
    }

    public long getElapsed() {
        return elapsed;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }
}
