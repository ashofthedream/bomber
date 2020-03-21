package ashes.of.bomber.core;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BooleanSupplier;


public class State {

    private final Stage stage;
    private final Settings settings;
    private final String testSuite;

    private volatile Instant testSuiteStartTime = Instant.EPOCH;
    private volatile Instant testCaseStartTime = Instant.EPOCH;

    private final AtomicLong testCaseRemainTotalInvocationCount = new AtomicLong(0);
    private final LongAdder errorCount = new LongAdder();

    public State(Stage stage, Settings settings, String testSuite) {
        this.stage = stage;
        this.settings = settings;
        this.testSuite = testSuite;
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

    public Instant getTestSuiteStartTime() {
        return testSuiteStartTime;
    }


    public boolean isSuiteStated() {
        return !testSuiteStartTime.equals(Instant.EPOCH);
    }

    public void startSuiteIfNotStarted() {
        if (!isSuiteStated()) {
            testSuiteStartTime = Instant.now();
        }
    }

    public boolean isCaseStated() {
        return !testCaseStartTime.equals(Instant.EPOCH);
    }

    public void startCaseIfNotStarted() {
        if (!isCaseStated()) {
            testCaseStartTime = Instant.now();
            testCaseRemainTotalInvocationCount.set(settings.getTotalInvocationsCount());
        }
    }

    public void finishCase() {
        testCaseStartTime = Instant.EPOCH;
    }


    public void incError() {
        errorCount.increment();
    }

    public long getRemainInvocations() {
        return testCaseRemainTotalInvocationCount.get();
    }

    public long getCaseRemainTime() {
        return (testCaseStartTime.toEpochMilli() + settings.getTime().toMillis()) - System.currentTimeMillis();
    }

    public long getCaseElapsedTime() {
        return System.currentTimeMillis() - testCaseStartTime.toEpochMilli();
    }

    public long getSuiteElapsedTime() {
        return System.currentTimeMillis() - testSuiteStartTime.toEpochMilli();
    }

    public long getErrorCount() {
        return errorCount.sum();
    }

    public BooleanSupplier createChecker() {
        AtomicLong threadRemainInvocations = new AtomicLong(settings.getThreadInvocationsCount());

        return () -> check(threadRemainInvocations);
    }

    private boolean check(AtomicLong threadRemainInvs) {
        return testCaseRemainTotalInvocationCount.decrementAndGet() >= 0 &&
                threadRemainInvs.decrementAndGet() >= 0 &&
                getCaseRemainTime() >= 0;
    }
}
