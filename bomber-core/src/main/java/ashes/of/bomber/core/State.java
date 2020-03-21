package ashes.of.bomber.core;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BooleanSupplier;


public class State {

    private final Stage stage;
    private final Settings settings;
    private final String testSuite;

    private volatile Instant startTime = Instant.EPOCH;

    private final AtomicLong totalRemainInvs;
    private final LongAdder errorCount = new LongAdder();

    public State(Stage stage, Settings settings, String testSuite) {
        this.stage = stage;
        this.settings = settings;
        this.testSuite = testSuite;

        this.totalRemainInvs = new AtomicLong(settings.getTotalInvocationsCount());
    }

    public String getTestSuite() {
        return testSuite;
    }

    public Stage getStage() {
        return stage;
    }

    public Settings getSettings() {
        return settings;
    }

    public Instant getStartTime() {
        return startTime;
    }

    boolean isStated() {
        return !startTime.equals(Instant.EPOCH);
    }

    public void startIfNotStarted() {
        if (!isStated()) {
            startTime = Instant.now();
        }
    }

    public void incError() {
        errorCount.increment();
    }

    public long getRemainInvocations() {
        return totalRemainInvs.get();
    }

    public long getRemainTime() {
        return (startTime.toEpochMilli() + settings.getTime().toMillis()) - System.currentTimeMillis();
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime.toEpochMilli();
    }

    public long getErrorCount() {
        return errorCount.sum();
    }

    public BooleanSupplier createChecker() {
        AtomicLong threadRemainInvocations = new AtomicLong(settings.getThreadInvocationsCount());

        return () -> check(threadRemainInvocations);
    }

    private boolean check(AtomicLong threadRemainInvs) {
        return totalRemainInvs.decrementAndGet() >= 0 &&
                threadRemainInvs.decrementAndGet() >= 0 &&
                getRemainTime() >= 0;
    }
}
