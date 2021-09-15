package ashes.of.bomber.runner;

import ashes.of.bomber.configuration.Settings;
import ashes.of.bomber.configuration.Stage;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.squadron.Barrier;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

public class WorkerState {

    private final RunnerState runnerState;
    private final CountDownLatch startLatch;
    private final CountDownLatch endLatch;
    private final Sink sink;
    private final Barrier barrier;

    private final AtomicLong iterationsCountSeq = new AtomicLong();

    private final AtomicLong expectedRecordsCount = new AtomicLong();
    private final AtomicLong caughtRecordsCount = new AtomicLong();
    private final AtomicLong errorsCount = new AtomicLong();

    private final AtomicLong remainIterationsCount = new AtomicLong();

    public WorkerState(RunnerState state, CountDownLatch startLatch, CountDownLatch endLatch, Sink sink, Barrier barrier) {
        this.runnerState = state;
        this.startLatch = startLatch;
        this.endLatch = endLatch;
        this.sink = sink;
        this.barrier = barrier;
    }

    public RunnerState getRunnerState() {
        return runnerState;
    }

    public CountDownLatch getStartLatch() {
        return startLatch;
    }

    public CountDownLatch getEndLatch() {
        return endLatch;
    }

    public Sink getSink() {
        return sink;
    }

    public Barrier getBarrier() {
        return barrier;
    }

    public long nextIterationNumber() {
        return iterationsCountSeq.getAndIncrement();
    }

    public long getCurrentIterationsCount() {
        return iterationsCountSeq.get();
    }

    public long getRemainIterationsCount() {
        return remainIterationsCount.get();
    }

    public long getErrorsCount() {
        return errorsCount.get();
    }

    public String getTestSuite() {
        return runnerState.getTestSuite();
    }

    public Stage getStage() {
        return runnerState.getStage();
    }

    public Instant getTestCaseStartTime() {
        return runnerState.getTestCaseStartTime();
    }

    public void startCaseIfNotStarted(String name, Stage stage, Settings settings) {
        remainIterationsCount.set(settings.getThreadIterationsCount());
        runnerState.startCaseIfNotStarted(name, stage, settings);
    }

    public void finishCase() {
        runnerState.finishCase();
    }

    public void incError() {
        errorsCount.incrementAndGet();
        runnerState.incError();
    }

    public BooleanSupplier createChecker() {
        BooleanSupplier checker = runnerState.createChecker();
        return () -> check(checker);
    }

    private boolean check(BooleanSupplier checker) {
        return remainIterationsCount.decrementAndGet() >= 0 && checker.getAsBoolean();
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
