package ashes.of.bomber.runner;

import ashes.of.bomber.configuration.Configuration;
import ashes.of.bomber.core.TestApp;
import ashes.of.bomber.core.TestCase;
import ashes.of.bomber.core.TestSuite;
import ashes.of.bomber.flight.plan.TestCasePlan;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

public class TestCaseState {
    private final TestSuiteState parent;
    private final TestCasePlan plan;
    private final TestCase<Object> testCase;

    private final Configuration configuration;

    private final Instant startTime = Instant.now();
    private volatile Instant finishTime;

    private final AtomicLong lastUpdated = new AtomicLong();
    private final AtomicLong iterationsCount = new AtomicLong();
    private final AtomicLong errorsCount = new AtomicLong();

    private final CountDownLatch startLatch;
    private final Phaser finishLatch;

    public TestCaseState(TestSuiteState parent, TestCasePlan plan, TestCase<Object> testCase, Configuration configuration) {
        this.parent = parent;
        this.plan = plan;
        this.testCase = testCase;
        this.configuration = configuration;
        this.startLatch = new CountDownLatch(configuration.settings().threads());
        this.finishLatch = new Phaser();
    }

    public TestApp getTestApp() {
        return parent.getAppState().getTestApp();
    }

    public TestSuite<Object> getTestSuite() {
        return parent.getTestSuite();
    }

    public TestCase<Object> getTestCase() {
        return testCase;
    }

    public TestSuiteState getSuiteState() {
        return parent;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getFinishTime() {
        return finishTime;
    }

    public CountDownLatch getStartLatch() {
        return startLatch;
    }

    public Phaser getFinishLatch() {
        return finishLatch;
    }

    public long getFlightId() {
        return parent.getFlightId();
    }



    public void finish() {
        this.finishTime = Instant.now();
    }

    public void awaitFinish() throws InterruptedException {
        var phase = finishLatch.getPhase();
        finishLatch.awaitAdvance(phase);
    }

    public boolean needUpdate() {
        var now = System.currentTimeMillis() / 1000;
        var last = lastUpdated.get();
        return now != last && lastUpdated.compareAndSet(last, now);
    }

    public long getIterationsCount() {
        return iterationsCount.get();
    }

    public long getErrorCount() {
        return errorsCount.get();
    }

    public void addError() {
        errorsCount.incrementAndGet();
    }

    public BooleanSupplier getCondition() {
        var parent = this.parent.getAppState().getFlightState().getCondition();
        return () -> parent.getAsBoolean() && incAndCheckTotalIterations();
    }

    private boolean incAndCheckTotalIterations() {
        return configuration.settings().iterations() >= iterationsCount.incrementAndGet();
    }
}
