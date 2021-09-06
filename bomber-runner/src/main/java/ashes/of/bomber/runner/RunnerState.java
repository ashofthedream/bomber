package ashes.of.bomber.runner;

import ashes.of.bomber.flight.Settings;
import ashes.of.bomber.flight.SettingsBuilder;
import ashes.of.bomber.flight.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BooleanSupplier;


public class RunnerState {
    private static final Logger log = LogManager.getLogger();

    private volatile Stage stage = Stage.IDLE;
    private volatile Settings settings = SettingsBuilder.disabled();

    private volatile String testSuite;
    private volatile String testCase;

    private volatile Instant testSuiteStartTime = Instant.EPOCH;
    private volatile Instant testCaseStartTime = Instant.EPOCH;

    private final AtomicLong remainItCount = new AtomicLong();
    private final LongAdder errorCount = new LongAdder();

    private final AtomicBoolean needCallSinkBeforeTestCase = new AtomicBoolean();
    private final AtomicBoolean needCallSinkAfterTestCase = new AtomicBoolean();

    private final AtomicLong lastUpdated = new AtomicLong();
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
            this.needCallSinkBeforeTestCase.set(true);
            this.needCallSinkAfterTestCase.set(true);
            this.testCase = testCase;
            this.stage = stage;
            this.settings = settings;
            this.testCaseStartTime = Instant.now();
            this.remainItCount.set(settings.getTotalIterationsCount());
        }
    }

    public void finishCase() {
        testCase = "";
        stage = Stage.IDLE;
        settings = SettingsBuilder.disabled();
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


    public boolean needCallSinkBeforeTestCase() {
        return needCallSinkBeforeTestCase.compareAndSet(true, false);
    }

    public boolean needCallSinkAfterTestCase() {
        return needCallSinkAfterTestCase.compareAndSet(true, false);
    }

    public boolean needUpdate() {
        var now = System.currentTimeMillis() / 1000;
        var last = lastUpdated.get();
        return now != last && lastUpdated.compareAndSet(last, now);
    }
}
