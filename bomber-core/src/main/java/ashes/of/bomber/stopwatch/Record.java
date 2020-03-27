package ashes.of.bomber.stopwatch;

import ashes.of.bomber.core.Iteration;

import javax.annotation.Nullable;

public class Record {
    private final Iteration it;
    private final String label;
    private final long timestamp;
    private final long elapsed;
    private final boolean success;

    @Nullable
    private final Throwable error;

    public Record(Iteration it, String label, long timestamp, long elapsed, boolean success, @Nullable Throwable error) {
        this.it = it;
        this.label = label;
        this.timestamp = timestamp;
        this.elapsed = elapsed;
        this.success = success;
        this.error = error;
    }

    public Iteration getIteration() {
        return it;
    }

    public String getLabel() {
        return label;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getElapsed() {
        return elapsed;
    }

    public boolean isSuccess() {
        return success;
    }

    @Nullable
    public Throwable getError() {
        return error;
    }
}
