package ashes.of.bomber.runner;

import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;

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

    public WorkerState(State state, Settings settings) {
        this.state = state;
        this.settings = settings;
    }

    public long nextItNumber() {
        return itNumberSeq.getAndIncrement();
    }

    public long currentItNumber() {
        return itNumberSeq.get();
    }

    public long getErrorsCount() {
        return errorCount.sum();
    }

    public long getRemainIterationsCount() {
        return remainItCount.get();
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

    public void startCaseIfNotStarted(String name, Stage stage, Settings settings) {
        remainItCount.set(settings.getThreadIterationsCount());
        state.startCaseIfNotStarted(name, stage, settings);
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
