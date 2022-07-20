package ashes.of.bomber.runner;

import ashes.of.bomber.configuration.Configuration;
import ashes.of.bomber.core.TestApp;
import ashes.of.bomber.core.TestCase;
import ashes.of.bomber.core.TestSuite;
import ashes.of.bomber.flight.plan.TestCasePlan;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
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
    private final AtomicLong totalIterationsCount = new AtomicLong();
    private final AtomicLong errorsCount = new AtomicLong();

    private final CountDownLatch startLatch;
    private final CountDownLatch finishLatch;

    public TestCaseState(TestSuiteState parent, TestCasePlan plan, TestCase<Object> testCase, Configuration configuration) {
        this.parent = parent;
        this.plan = plan;
        this.testCase = testCase;
        this.configuration = configuration;
        this.startLatch = new CountDownLatch(configuration.settings().threads());
        this.finishLatch = new CountDownLatch(configuration.settings().threads());
    }

    public TestApp getTestApp() {
        return parent.getParent().getTestApp();
    }

    public TestSuite<Object> getTestSuite() {
        return parent.getTestSuite();
    }

    public TestCase<Object> getTestCase() {
        return testCase;
    }

    public TestSuiteState getParent() {
        return parent;
    }

    public TestCasePlan getPlan() {
        return plan;
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

    public CountDownLatch getFinishLatch() {
        return finishLatch;
    }

    public long getFlightId() {
        return parent.getFlightId();
    }



    public void finish() {
        this.finishTime = Instant.now();
    }

    public void awaitFinish() throws InterruptedException {
        finishLatch.await();
    }

    public boolean needUpdate() {
        var now = System.currentTimeMillis() / 1000;
        var last = lastUpdated.get();
        return now != last && lastUpdated.compareAndSet(last, now);
    }

    public long getTotalIterationsCount() {
        return totalIterationsCount.get();
    }

    public long getErrorCount() {
        return errorsCount.get();
    }

    public void addError() {
        errorsCount.incrementAndGet();
    }

    public BooleanSupplier getCondition() {
        var parent = this.parent.getParent().getParent().getCondition();
        return () -> parent.getAsBoolean() && incAndCheckTotalIterations();
    }

    private boolean incAndCheckTotalIterations() {
        return configuration.settings().iterations() >= totalIterationsCount.incrementAndGet();
    }
}
