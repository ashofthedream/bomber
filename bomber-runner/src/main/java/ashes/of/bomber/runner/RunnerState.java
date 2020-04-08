package ashes.of.bomber.runner;

import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BooleanSupplier;


public class RunnerState {
    private static final Logger log = LogManager.getLogger();

    private volatile Stage stage = Stage.Idle;
    private volatile Settings settings = new Settings();

    private volatile String testSuite;
    private volatile String testCase;

    private volatile Instant testSuiteStartTime = Instant.EPOCH;
    private volatile Instant testCaseStartTime = Instant.EPOCH;

    private final AtomicLong remainItCount = new AtomicLong(0);
    private final LongAdder errorCount = new LongAdder();

    private final BooleanSupplier shutdown;

    public RunnerState(BooleanSupplier shutdown) {
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

    public void setSettings(Settings settings) {
        this.settings = settings;
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

    public void startSuiteIfNotStarted(String name) {
        if (!isSuiteStated()) {
            testSuite = name;
            testSuiteStartTime = Instant.now();
        }
    }

    public void finishSuite() {
        testSuite = "";
        finishCase();
    }

    public boolean isCaseStated() {
        return !testCaseStartTime.equals(Instant.EPOCH);
    }

    public void startCaseIfNotStarted(String testCase, Stage stage, Settings settings) {
        if (!isCaseStated()) {
            this.testCase = testCase;
            this.stage = stage;
            this.settings = settings;
            this.testCaseStartTime = Instant.now();
            this.remainItCount.set(settings.getTotalIterationsCount());
        }
    }

    public void finishCase() {
        testCase = "";
        stage = Stage.Idle;
        settings = new Settings().disabled();
        testCaseStartTime = Instant.EPOCH;
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
