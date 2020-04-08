package ashes.of.bomber.runner;

import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

public class WorkerState {

    private final RunnerState state;

    private final AtomicLong itCount = new AtomicLong();
    private final AtomicLong expectedRecordsCount = new AtomicLong();
    private final AtomicLong caughtRecordsCount = new AtomicLong();
    private final AtomicLong errorCount = new AtomicLong();

    private final AtomicLong remainItCount = new AtomicLong();

    public WorkerState(RunnerState state) {
        this.state = state;
    }

    public long nextItNumber() {
        return itCount.getAndIncrement();
    }

    public long getCurrentIterationsCount() {
        return itCount.get();
    }

    public long getRemainIterationsCount() {
        return remainItCount.get();
    }

    public long getErrorsCount() {
        return errorCount.get();
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
        errorCount.incrementAndGet();
        state.incError();
    }

    public BooleanSupplier createChecker() {
        BooleanSupplier checker = state.createChecker();
        return () -> check(checker);
    }

    private boolean check(BooleanSupplier checker) {
        return remainItCount.decrementAndGet() >= 0 && checker.getAsBoolean();
    }

    public long getExpectedRecordsCount() {
        return expectedRecordsCount.get();
    }

    public long getCaughtRecordsCount() {
        return caughtRecordsCount.get();
    }

    public void addExpectedCount(long count) {
        expectedRecordsCount.addAndGet(count);
    }

    public void addCaughtCount(long count) {
        caughtRecordsCount.addAndGet(count);
    }
}
