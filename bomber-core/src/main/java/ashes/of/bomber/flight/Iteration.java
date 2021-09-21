package ashes.of.bomber.flight;

import ashes.of.bomber.core.Test;

import java.time.Instant;

/**
 * todo rename it, an make it as part of TestCaseBeforeEachEvent and TestCaseAfterEachEvent
 */
public class Iteration {
    private final long flightId;
    private final long number;
    private final Test test;
    private final String thread;
    private final Instant timestamp;

    public Iteration(long flightId, long number, String thread, Instant timestamp, Test test) {
        this.flightId = flightId;
        this.number = number;
        this.test = test;
        this.thread = thread;
        this.timestamp = timestamp;
    }

    public long getFlightId() {
        return flightId;
    }

    public long getNumber() {
        return number;
    }

    public Test getTest() {
        return test;
    }

    public String getThread() {
        return thread;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Iteration{" +
                "number=" + number +
                ", test=" + test +
                ", thread='" + thread + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
