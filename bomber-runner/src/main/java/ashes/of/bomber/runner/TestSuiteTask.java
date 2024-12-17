package ashes.of.bomber.runner;

import ashes.of.bomber.configuration.Configuration;
import ashes.of.bomber.core.TestCase;
import ashes.of.bomber.core.TestSuite;
import ashes.of.bomber.events.EventMachine;
import ashes.of.bomber.events.TestSuiteFinishedEvent;
import ashes.of.bomber.events.TestSuiteStartedEvent;
import ashes.of.bomber.flight.plan.TestCasePlan;
import ashes.of.bomber.flight.plan.TestSuitePlan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

public class TestSuiteTask implements Runnable {
    private static final Logger log = LogManager.getLogger();

    private final TestFlightTask parent;
    private final EventMachine em;
    private final TestSuitePlan plan;
    private final TestSuite<Object> testSuite;

    private volatile WorkerPool executor;
    private volatile Instant startTime;
    private volatile Instant finishTime;

    private final List<TestCaseTask> children = new CopyOnWriteArrayList<>();

    public TestSuiteTask(TestFlightTask parent, EventMachine em, TestSuitePlan plan, TestSuite<Object> testSuite) {
        this.parent = parent;
        this.em = em;
        this.plan = plan;
        this.testSuite = testSuite;
    }

    public WorkerPool getExecutor() {
        return executor;
    }

    public TestSuiteTask setExecutor(WorkerPool executor) {
        this.executor = executor;
        return this;
    }

    public TestFlightTask getAppState() {
        return parent;
    }

    public TestSuitePlan getPlan() {
        return plan;
    }

    public TestSuite<Object> getTestSuite() {
        return testSuite;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getFinishTime() {
        return finishTime;
    }

    public long getFlightId() {
        return parent.getPlan().flightId();
    }

    public TestCaseTask getCurrentChild() {
        if (children.isEmpty()) {
            return null;
        }

        return children.get(children.size() - 1);
    }

    /**
     * Runs the test suite
     */
    @Override
    public void run() {
        ThreadContext.put("testSuite", testSuite.name());
        log.info("Start test suite: {}", testSuite.name());

        log.trace("Reset before & after test suite lifecycle methods");
        testSuite.resetBeforeAndAfterSuite();

        startTime = Instant.now();
        em.dispatch(new TestSuiteStartedEvent(startTime, getFlightId(), null, testSuite.name()));

        int threads = determineWorkerThreadsCount();
        executor = new WorkerPool(em, threads);

        try {
            callBeforeSuite();

            plan.testCases()
                    .forEach(plan -> {
                        var testCase = testSuite.getTestCase(plan.name());
                        if (testCase == null) {
                            log.warn("Test case: {} not found in test suite: {}, but it exists in the plan",
                                    plan.name(), testSuite.name());
                            return;
                        }

                        // merge configuration
                        var merged = mergeConfiguration(plan, testCase);
                        var child = new TestCaseTask(this, executor, em, plan, testCase, merged);
                        children.add(child);
                        child.run();
                    });

            callAfterSuite();

            log.info("Finish test suite: {}", testSuite.name());
            finishTime = Instant.now();
            em.dispatch(new TestSuiteFinishedEvent(finishTime, getFlightId(), null, testSuite.name()));

        } catch (Throwable throwable) {
            log.error("Run test suite: {} failed", testSuite.name(), throwable);
        } finally {
            executor.shutdown();
            ThreadContext.remove("testSuite");
        }
    }

    private Configuration mergeConfiguration(TestCasePlan plan, TestCase<Object> testCase) {
        var initial = testCase.configuration();
        return Optional.ofNullable(plan.configuration())
                .map(actual -> new Configuration(
                        // todo get these properties from actual
                        initial.delayer(),
                        initial.limiter(),
                        initial.barrier(),
                        actual.settings()
                ))
                .orElse(initial);

    }

    private int determineWorkerThreadsCount() {
        log.debug("Determine worker threads count for test suite: {}", testSuite.name());
        return plan.testCases().stream()
                .mapToInt(testCasePlan -> {
                    var testCase = testSuite.getTestCase(testCasePlan.name());
                    if (testCase == null) {
                        log.trace("No test case: {} found, return 0", testCasePlan.name());
                        return 0;
                    }

                    var config = Optional.ofNullable(testCasePlan.configuration())
                            .orElse(testCase.configuration());

                    return config.settings().threads();
                })
                .max()
                .orElseThrow(() -> new RuntimeException("Can't determine thread count for test suite: " + testSuite.name()));
    }

    private void callBeforeSuite() throws InterruptedException {
        var workers = executor.getWorkers();
        log.debug("Call beforeSuite for all workers: {}", workers.size());
        var latch = new CountDownLatch(workers.size());
        workers.forEach(worker -> {
            worker.run(() -> worker.runBeforeSuite(testSuite, latch));
        });

        log.debug("Await {} workers beforeSuite", workers.size());
        latch.await();
    }

    private void callAfterSuite() throws InterruptedException {
        var workers = executor.getWorkers();
        log.debug("Call afterSuite for all workers: {}", workers.size());
        var latch = new CountDownLatch(workers.size());
        workers.forEach(worker -> {
            worker.run(() -> worker.runAfterSuite(testSuite, latch));
        });

        log.debug("Await {} workers afterSuite", workers.size());
        latch.await();
    }
}
