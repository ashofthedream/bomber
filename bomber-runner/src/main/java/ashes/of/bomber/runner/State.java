package ashes.of.bomber.runner;

import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BooleanSupplier;


public class State {

    private final Stage stage;
    private final Settings settings;
    private final String testSuite;

    @Nullable
    private volatile String testCase;

    private volatile Instant testSuiteStartTime = Instant.EPOCH;
    private volatile Instant testCaseStartTime = Instant.EPOCH;

    private final AtomicLong remainItCount = new AtomicLong(0);
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

    @Nullable
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

    public Instant getTestCaseStartTime() {
        return testCaseStartTime;
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
            remainItCount.set(settings.getTotalIterationsCount());
        }
    }

    public void finishCase() {
        testCaseStartTime = Instant.EPOCH;
        testCase = null;
    }


    public void incError() {
        errorCount.increment();
    }

    public long getTotalIterationsRemain() {
        return remainItCount.get();
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
        long deadline = System.currentTimeMillis() + getCaseRemainTime();
        return () -> check(deadline);
    }

    private boolean check(long deadline) {
        return !shutdown.getAsBoolean() &&
                remainItCount.decrementAndGet() >= 0 &&
                System.currentTimeMillis() < deadline;
    }
}
