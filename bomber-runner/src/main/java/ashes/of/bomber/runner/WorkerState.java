package ashes.of.bomber.runner;

import ashes.of.bomber.core.Test;
import ashes.of.bomber.flight.Iteration;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

public class WorkerState {

    private final TestCaseState parent;

    private final AtomicLong itSeq = new AtomicLong();
    private final AtomicLong expectedRecordsCount = new AtomicLong();
    private final AtomicLong caughtRecordsCount = new AtomicLong();
    private final AtomicLong iterationsCount = new AtomicLong();
    private final AtomicLong errorsCount = new AtomicLong();
    private volatile boolean finished = false;

    public WorkerState(TestCaseState parent) {
        this.parent = parent;
    }

    public TestCaseState getParent() {
        return parent;
    }


    public long getIterationsCount() {
        return iterationsCount.get();
    }

    public Iteration createIteration() {
        String threadName = Thread.currentThread().getName();
        var test = new Test(
                parent.getTestApp().getName(),
                parent.getTestSuite().getName(),
                parent.getTestCase().getName());

        return new Iteration(
                parent.getFlightId(),
                itSeq.get(),
                threadName,
                Instant.now(),
                test);
    }


    public long getErrorsCount() {
        return errorsCount.get();
    }


    public void addSuccess() {
        iterationsCount.incrementAndGet();
    }

    public void addError() {
        iterationsCount.incrementAndGet();
        errorsCount.incrementAndGet();
        parent.addError();
    }

    public long getExpectedRecordsCount() {
        return expectedRecordsCount.get();
    }

    public void addExpectedCount(long count) {
        expectedRecordsCount.addAndGet(count);
    }


    public long getCaughtRecordsCount() {
        return caughtRecordsCount.get();
    }

    public void addCaughtCount(long count) {
        caughtRecordsCount.addAndGet(count);
    }

    public BooleanSupplier createCondition() {
        var condition = parent.getCondition();
        return () -> condition.getAsBoolean() && checkTime() && !finished;
    }

    private boolean checkTime() {
        var settings = parent.getConfiguration().settings();
        var elapsed = System.currentTimeMillis() - parent.getStartTime().toEpochMilli();
        return settings.duration().toMillis() >= elapsed;
    }

    public void start() {
        parent.getStartLatch().countDown();
    }

    public boolean awaitStart(int seconds) throws InterruptedException {
        return parent.getStartLatch().await(seconds, TimeUnit.SECONDS);
    }

    public void finish() {
        parent.getFinishLatch().arrive();
        finished = true;
    }

    public void markFinished() {
        finished = true;
    }

}
