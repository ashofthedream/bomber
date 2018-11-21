package ashes.of.loadtest.runner;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import ashes.of.loadtest.Stage;
import ashes.of.loadtest.Test;
import ashes.of.loadtest.TestCase;
import ashes.of.loadtest.settings.Settings;
import ashes.of.loadtest.sink.Sink;
import ashes.of.loadtest.stopwatch.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

import static java.util.concurrent.TimeUnit.SECONDS;


public class TestCaseRunner<T extends TestCase> {
    private static final Logger log = LogManager.getLogger(TestCaseRunner.class);

    private final String testCaseName;
    private final Stage stage;
    private final Settings settings;

    private final AtomicLong totalRemainInvs;
    private final LongAdder errorCount = new LongAdder();
    private volatile Instant startTime = Instant.EPOCH;

    private final List<Sink> sinks;
    private final Supplier<T> testCase;
    private final Map<String, Test<T>> tests;



    public TestCaseRunner(String testCaseName,
                          Stage stage,
                          Settings settings,
                          List<Sink> sinks,
                          Map<String, Test<T>> tests,
                          Supplier<T> testCase) {
        this.testCaseName = testCaseName;
        this.stage = stage;
        this.settings = new Settings(settings);
        this.totalRemainInvs = new AtomicLong(settings.getTotalInvocations());
        this.testCase = testCase;
        this.sinks = sinks;
        this.tests = tests;
    }

    public Stage getStage() {
        return stage;
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    public long getRemainOps() {
        return totalRemainInvs.get();
    }

    public Instant getStartTime() {
        return startTime;
    }

    public long getRemainTime() {
        return (startTime.toEpochMilli() + settings.getTime().toMillis()) - System.currentTimeMillis();
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime.toEpochMilli();
    }

    public long getErrorCount() {
        return errorCount.sum();
    }


    /**
     * Runs the test case
     */
    public void run() {
        if (settings.isDisabled()) {
            log.info("Ignore disabled stage: {}, test: {}", stage, testCaseName);
            return;
        }

        startTime = Instant.now();
        log.info("start stage: {}, test: {}, threads: {}, iterations total: {} per thread: {}, duration: {}",
                stage, testCaseName, settings.getThreads(), settings.getTotalInvocations(), settings.getThreadInvocations(), settings.getTime());

        sinks.forEach(sink -> sink.beforeRun(stage, testCaseName, startTime, settings));

        CountDownLatch startLatch = new CountDownLatch(settings.getThreads());
        CountDownLatch endLatch = new CountDownLatch(settings.getThreads());

        startWatchdogThread(startLatch, endLatch);
        startWorkerThreads(startLatch, endLatch);

        try {
            log.info("await for end of stage: {}, test: {}, elapsed {}ms", stage, testCaseName, getElapsedTime());
            endLatch.await();
        } catch (InterruptedException e) {
            log.error("We'he been interrupted", e);
        }

        log.info("end stage: {}, test: {}, elapsed {}ms", stage, testCaseName, getElapsedTime());
        sinks.forEach(sink -> sink.afterRun(stage, testCaseName, startTime, settings));
    }


    private void startWatchdogThread(CountDownLatch startLatch, CountDownLatch endLatch) {
        new Watchdog(this, startLatch, endLatch).startInNewThread();
    }

    private void startWorkerThreads(CountDownLatch startLatch, CountDownLatch endLatch) {
        for (int i = 0; i < settings.getThreads(); i++)
            startWorkerThread(() -> runTestCase(startLatch, endLatch), i);
    }

    private void startWorkerThread(Runnable runnable, int index) {
        Thread thread = new Thread(runnable);
        thread.setName(String.format("%s-%s-worker-%03d", testCaseName, stage.name(), index));
        thread.start();
    }

    private void runTestCase(CountDownLatch startLatch, CountDownLatch endLatch) {
        try {
            T testCase = this.testCase.get();
            BooleanSupplier checker = runChecker();

            startLatch.countDown();

            // if we can't start in 60 seconds – something works bad
            startLatch.await(30, SECONDS);

            runTestCase(testCase, checker);
        } catch (Throwable e) {
            log.warn("runTestCase test: {}, stage: {} failed", testCaseName, stage, e);
        } finally {
            endLatch.countDown();
        }
    }

    private BooleanSupplier runChecker() {
        AtomicLong threadRemainInvs = new AtomicLong(settings.getThreadInvocations());
        return () ->
                totalRemainInvs.decrementAndGet() >= 0 &&
                threadRemainInvs.decrementAndGet() >= 0 &&
                getRemainTime() >= 0;
    }

    private void runTestCase(T testCase, BooleanSupplier checker) throws Exception {
        log.trace("beforeAll stage: {}, test: {}", stage, testCaseName);
        String threadName = Thread.currentThread().getName();
        AtomicLong invocations = new AtomicLong();

        testCase.beforeAll();
        while (checker.getAsBoolean()) {
            long inv = invocations.getAndIncrement();
            TestCaseContext context = new TestCaseContext(stage, testCaseName, threadName, inv, Instant.now());

            long start = System.nanoTime();
            tests.forEach((name, test) -> test(context, name, testCase, test));
            long elapsed = System.nanoTime() - start;

            sinkAfterTests(context, elapsed);
        }

        log.trace("afterAll stage: {}, test: {}, iterations: {}", stage, testCaseName, invocations);
        testCase.afterAll();
    }

    private void sinkAfterTests(TestCaseContext context, long elapsed) {
        sinks.forEach(sink -> sink.afterTests(context, elapsed));
    }


    private void test(TestCaseContext tcc, String testName, T testCase, Test<T> test) {
        TestContext context = new TestContext(tcc, testName, Instant.now());
        log.trace("test stage: {}, testCase: {}, test: {}, inv: {}", stage,
                tcc.getName(), context.getName(), tcc.getInvocationNumber());

        beforeTest(context, testCase);

        Stopwatch stopwatch = new Stopwatch();
        try {
            // test
            test.run(testCase, stopwatch);

            sinkAfterTest(context, stopwatch.elapsed(), stopwatch, null);
        } catch (Throwable th) {
            log.debug("test stage: {}, testCase: {}, test: {}, inv: {} failed", stage,
                    tcc.getName(), context.getName(), tcc.getInvocationNumber(), th);
            errorCount.increment();
            sinkAfterTest(context, stopwatch.elapsed(), stopwatch, th);
        }

        afterTest(context, testCase);
    }

    private void afterTest(TestContext context, T testCase) {
        try {
            log.trace("afterTest test: {}, inv: {}", context.getName(), context.getInvocationNumber());
            testCase.afterTest();
        } catch (Exception e) {
            log.warn("beforeTest test: {}, inv: {} failed. ", testCaseName, context.getInvocationNumber(), e);
        }
    }

    private void beforeTest(TestContext context, T testCase) {
        try {
            log.trace("beforeTest test: {}, inv: {}", testCaseName, context.getInvocationNumber());
            testCase.beforeTest();
        } catch (Exception e) {
            log.warn("beforeTest test: {}, inv: {} failed. ", testCaseName, context.getInvocationNumber(), e);
        }
    }

    private void sinkAfterTest(TestContext context, long elapsed, Stopwatch stopwatch, @Nullable Throwable th) {
        sinks.forEach(sink -> sink.afterTest(context, elapsed, stopwatch, th));
    }


    @Override
    public String toString() {
        return "TestCaseRunner{" +
                "testCaseName='" + testCaseName + '\'' +
                '}';
    }
}
