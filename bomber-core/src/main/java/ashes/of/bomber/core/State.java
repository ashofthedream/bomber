package ashes.of.bomber.core;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BooleanSupplier;


public class State {

    private final Stage stage;
    private final Settings settings;
    private final String testSuite;
    private volatile String testCase;

    private volatile Instant testSuiteStartTime = Instant.EPOCH;
    private volatile Instant testCaseStartTime = Instant.EPOCH;

    private final AtomicLong testCaseRemainTotalInvocationCount = new AtomicLong(0);
    private final LongAdder errorCount = new LongAdder();

    private final BooleanSupplier shutdown;

    public State(Stage stage, Settings settings, String testSuite, BooleanSupplier shutdown) {
        this.stage = stage;
        this.settings = settings;
        this.testSuite = testSuite;
        this.shutdown = shutdown;
    }

    public String getTestSuite() {
        return testSuite;
    }

    public String getTestCase() {
        return testCase;
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

    public void startCaseIfNotStarted(String name) {
        if (!isCaseStated()) {
            testCase = name;
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
        long now = System.currentTimeMillis();
        return ((isCaseStated() ? testCaseStartTime.toEpochMilli() : now) + settings.getTime().toMillis()) - now;
    }

    public long getCaseElapsedTime() {
        long now = System.currentTimeMillis();
        return now - (isCaseStated() ? testCaseStartTime.toEpochMilli() : now);
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
        return !shutdown.getAsBoolean() && testCaseRemainTotalInvocationCount.decrementAndGet() >= 0 &&
                threadRemainInvs.decrementAndGet() >= 0 &&
                getCaseRemainTime() >= 0;
    }
}
