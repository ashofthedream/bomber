package ashes.of.bomber.runner;

import ashes.of.bomber.core.Test;
import ashes.of.bomber.flight.Iteration;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

public class WorkerTask {

    private final TestCaseTask testCase;

    private final AtomicLong itSeq = new AtomicLong();
    private final AtomicLong expectedRecordsCount = new AtomicLong();
    private final AtomicLong caughtRecordsCount = new AtomicLong();
    private final AtomicLong iterationsCount = new AtomicLong();
    private final AtomicLong errorsCount = new AtomicLong();
    private volatile boolean finished = false;

    public WorkerTask(TestCaseTask testCase) {
        this.testCase = testCase;
    }

    public TestCaseTask getTestCase() {
        return testCase;
    }

    public long getIterationsCount() {
        return iterationsCount.get();
    }

    public Iteration createIteration() {
        String threadName = Thread.currentThread().getName();
        var test = new Test(
                null,
                testCase.getTestSuite().name(),
                testCase.getTestCase().name());

        return new Iteration(
                testCase.getFlightId(),
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
        testCase.addError();
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
        var condition = testCase.getCondition();
        return () -> condition.getAsBoolean() && checkTime() && !finished;
    }

    private boolean checkTime() {
        var settings = testCase.getConfiguration().settings();
        var elapsed = System.currentTimeMillis() - testCase.getStartTime().toEpochMilli();
        return settings.duration().toMillis() >= elapsed;
    }

    public void start() {
        testCase.getFinishLatch().register();
        testCase.getStartLatch().countDown();
    }

    public boolean awaitStart(int seconds) throws InterruptedException {
        return testCase.getStartLatch().await(seconds, TimeUnit.SECONDS);
    }

    public void finish() {
        testCase.getFinishLatch().arrive();
        finished = true;
    }

    public void markFinished() {
        finished = true;
    }

}
