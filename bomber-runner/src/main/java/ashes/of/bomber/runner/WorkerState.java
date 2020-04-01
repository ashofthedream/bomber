package ashes.of.bomber.runner;

import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;
import ashes.of.bomber.core.State;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BooleanSupplier;

public class WorkerState {

    private final Settings settings;
    private final State state;
    private final AtomicLong itNumberSeq = new AtomicLong();

    private final AtomicLong remainItCount = new AtomicLong();
    private final LongAdder errorCount = new LongAdder();

    public WorkerState(Settings settings, State state) {
        this.settings = settings;
        this.state = state;
    }

    public long nextItNumber() {
        return itNumberSeq.getAndIncrement();
    }

    public String getTestSuite() {
        return state.getTestSuite();
    }

    public Stage getStage() {
        return state.getStage();
    }

    public Instant getTestCaseStartTime() {
        return state.getTestCaseStartTime();
    }

    public void startCaseIfNotStarted(String name) {
        remainItCount.set(settings.getThreadIterationsCount());
        state.startCaseIfNotStarted(name);
    }

    public void finishCase() {
        state.finishCase();
    }

    public void incError() {
        errorCount.increment();
        state.incError();
    }

    public BooleanSupplier createChecker() {
        BooleanSupplier checker = state.createChecker();
        return () -> check(checker);
    }

    private boolean check(BooleanSupplier checker) {
        return remainItCount.decrementAndGet() >= 0 && checker.getAsBoolean();
    }
}
