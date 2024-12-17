package ashes.of.bomber.runner;

import ashes.of.bomber.configuration.Configuration;
import ashes.of.bomber.core.Test;
import ashes.of.bomber.core.TestSuiteGroup;
import ashes.of.bomber.core.TestCase;
import ashes.of.bomber.core.TestSuite;
import ashes.of.bomber.events.EventMachine;
import ashes.of.bomber.events.TestCaseFinishedEvent;
import ashes.of.bomber.events.TestCaseStartedEvent;
import ashes.of.bomber.flight.plan.TestCasePlan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

public class TestCaseTask implements Runnable {
    private static final Logger log = LogManager.getLogger();

    private final TestSuiteTask parent;
    private final WorkerPool executor;
    private final EventMachine em;
    private final TestCasePlan plan;
    private final TestCase<Object> testCase;

    private final Configuration configuration;

    private volatile Instant startTime;
    private volatile Instant finishTime;

    private final AtomicLong lastUpdated = new AtomicLong();
    private final AtomicLong iterationsCount = new AtomicLong();
    private final AtomicLong errorsCount = new AtomicLong();

    private final CountDownLatch startLatch;
    private final Phaser finishLatch;

    public TestCaseTask(TestSuiteTask parent, WorkerPool executor, EventMachine em, TestCasePlan plan, TestCase<Object> testCase, Configuration configuration) {
        this.parent = parent;
        this.executor = executor;
        this.em = em;
        this.plan = plan;
        this.testCase = testCase;
        this.configuration = configuration;
        this.startLatch = new CountDownLatch(configuration.settings().threads());
        this.finishLatch = new Phaser();
    }

    public TestSuite<Object> getTestSuite() {
        return parent.getTestSuite();
    }

    public TestCase<Object> getTestCase() {
        return testCase;
    }

    public TestSuiteTask getSuiteState() {
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

    public void onWorkerStart() {
        startLatch.countDown();
    }


    public Phaser getFinishLatch() {
        return finishLatch;
    }

    public long getFlightId() {
        return parent.getFlightId();
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
        var p = parent.getAppState().getCondition();
        return () -> p.getAsBoolean() && incAndCheckTotalIterations();
    }

    private boolean incAndCheckTotalIterations() {
        return configuration.settings().iterations() >= iterationsCount.incrementAndGet();
    }

    /**
     * Runs the test case
     */
    @Override
    public void run() {
        var testSuite = getTestSuite();

        var config = getConfiguration();
        var settings = config.settings();
        var barrier = config.barrier();

        startTime = Instant.now();
        ThreadContext.put("testCase", testCase.name());
        log.debug("Start test case: {}", testCase.name());

        log.trace("Reset beforeCase & afterCase lifecycle methods");
        testSuite.resetBeforeAndAfterCase();

        var test = new Test(null, testSuite.name(), testCase.name());
        em.dispatch(new TestCaseStartedEvent(startTime, getFlightId(), test, settings));

        barrier.init(settings.threads());

        log.debug("Run {} workers", settings.threads());

        executor.getWorkers()
                .stream()
                .limit(settings.threads())
                .forEach(worker -> worker.run(() -> worker.runTestCase(this)));

        try {
            log.debug("Await end of test case: {}", getTestCase().name());
            awaitFinish();

            log.debug("All workers done, 1s cooldown");
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            log.error("We've been interrupted", e);
        }


        log.info("Finish test case: {}", getTestCase().name());
        finishTime = Instant.now();
        em.dispatch(new TestCaseFinishedEvent(finishTime, getFlightId(), test));

        ThreadContext.remove("testCase");
    }
}
