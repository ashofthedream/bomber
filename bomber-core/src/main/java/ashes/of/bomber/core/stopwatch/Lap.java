package ashes.of.bomber.core.stopwatch;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class Lap {


    public static class Record {
        private final String label;
        private final long timestamp;
        private final long elapsed;
        private final boolean success;

        @Nullable
        private final String error;

        public Record(String label, long timestamp, long elapsed, boolean success, @Nullable String error) {
            this.label = label;
            this.timestamp = timestamp;
            this.elapsed = elapsed;
            this.success = success;
            this.error = error;
        }

        public Record(String label, long timestamp, long elapsed, boolean success) {
            this(label, timestamp, elapsed, success, null);
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

        @Nullable
        public String getError() {
            return error;
        }

        public boolean isSuccess() {
            return error == null;
        }
    }

    private volatile long init = System.nanoTime();

    private final String label;
    private final Consumer<Record> consumer;

    public Lap(String label, Consumer<Record> consumer) {
        this.label = label;
        this.consumer = consumer;
    }

    public Lap reset() {
        this.init = System.nanoTime();
        return this;
    }

    private Record record(boolean success, @Nullable String reason) {
        long init = this.init;
        long elapsed = System.nanoTime() - init;
        Record record = new Record(label, init, elapsed, success, reason);

        consumer.accept(record);

        return record;
    }

    public Record success() {
        return record(true, null);
    }

    public Record fail() {
        return record(false, null);
    }

    public Record fail(@Nullable Throwable th) {
        return record(false, th.getMessage());
    }

    public Record fail(@Nullable String reason) {
        return record(false, reason);

    }

    @Override
    public String toString() {
        return "Lap{" + label + "}";
    }
}
