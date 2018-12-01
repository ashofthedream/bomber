package ashes.of.loadtest.runner;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import ashes.of.loadtest.*;
import ashes.of.loadtest.settings.Settings;
import ashes.of.loadtest.sink.Sink;
import ashes.of.loadtest.stopwatch.Stopwatch;
import ashes.of.loadtest.throttler.Limiter;
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

    private final List<LifeCycle<T>> beforeAll;
    private final List<LifeCycle<T>> beforeEach;
    private final List<LifeCycle<T>> afterEach;
    private final List<LifeCycle<T>> afterAll;

    private final Map<String, TestWithStopwatch<T>> tests;
    private final Supplier<Limiter> throttler;


    public TestCaseRunner(String testCaseName,
                          Stage stage,
                          Settings settings,
                          List<Sink> sinks,
                          List<LifeCycle<T>> beforeAll,
                          List<LifeCycle<T>> beforeEach,
                          Map<String, TestWithStopwatch<T>> tests,
                          Supplier<T> testCase,
                          List<LifeCycle<T>> afterEach,
                          List<LifeCycle<T>> afterAll,
                          Supplier<Limiter> throttler) {
        this.testCaseName = testCaseName;
        this.stage = stage;
        this.settings = new Settings(settings);
        this.totalRemainInvs = new AtomicLong(settings.getTotalInvocationsCount());
        this.beforeAll = beforeAll;
        this.beforeEach = beforeEach;
        this.tests = tests;
        this.testCase = testCase;
        this.afterEach = afterEach;
        this.afterAll = afterAll;
        this.sinks = sinks;
        this.throttler = throttler;
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
                stage, testCaseName, settings.getThreads(), settings.getTotalInvocationsCount(), settings.getThreadInvocationsCount(), settings.getTime());

        sinks.forEach(sink -> sink.beforeAll(stage, testCaseName, startTime, settings));

        CountDownLatch begin = new CountDownLatch(settings.getThreads());
        CountDownLatch end = new CountDownLatch(settings.getThreads());

        startWatchdogThread(begin, end);
        startWorkerThreads(begin, end);

        try {
            log.info("await for end of stage: {}, test: {}, elapsed {}ms", stage, testCaseName, getElapsedTime());
            end.await();
        } catch (InterruptedException e) {
            log.error("We'he been interrupted", e);
        }

        log.info("end stage: {}, test: {}, elapsed {}ms", stage, testCaseName, getElapsedTime());
        sinks.forEach(sink -> sink.afterAll(stage, testCaseName, startTime, settings));
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
            Limiter limiter = this.throttler.get();

            startLatch.countDown();

            // if we can't start in 60 seconds – something works bad
            startLatch.await(30, SECONDS);

            log.trace("beforeAll stage: {}, test: {}", stage, testCaseName);
            beforeAll.forEach(l -> beforeAll(l, testCase));

            tests.forEach((name, test) -> runTest(name, testCase, test, limiter));

            log.trace("afterAll stage: {}, test: {}", stage, testCaseName);
            afterAll.forEach(l -> afterAll(l, testCase));

        } catch (Throwable e) {
            log.warn("runTestCase test: {}, stage: {} failed", testCaseName, stage, e);
        } finally {
            endLatch.countDown();
        }
    }


    private void runTest(String testName, T testCase, TestWithStopwatch<T> test, Limiter limiter) {
        String threadName = Thread.currentThread().getName();
        AtomicLong invocations = new AtomicLong();
        BooleanSupplier checker = checker();
        while (checker.getAsBoolean()) {
            if (!limiter.waitForAcquire())
                throw new RuntimeException("Limiter await failed");

            long inv = invocations.getAndIncrement();
            Context ctx = new Context(stage, testCaseName, testName, threadName, inv, Instant.now());

            log.trace("runTest stage: {}, testCase: {}, test: {}, inv: {}",
                    ctx.getStage(), ctx.getTestCase(), ctx.getTest(), ctx.getInv());

            beforeEach(ctx, testCase);

            Stopwatch stopwatch = new Stopwatch();
            try {
                // test
                test.run(testCase, stopwatch);

                sinkAfterEach(ctx, stopwatch.elapsed(), stopwatch, null);
            } catch (Throwable th) {
                log.debug("test stage: {}, testCase: {}, test: {}, inv: {} failed",
                        ctx.getStage(), ctx.getTestCase(), ctx.getTest(), ctx.getInv(), th);
                errorCount.increment();
                sinkAfterEach(ctx, stopwatch.elapsed(), stopwatch, th);
            }

            afterEach(ctx, testCase);
        }
    }

    private BooleanSupplier checker() {
        AtomicLong threadRemainInvs = new AtomicLong(settings.getThreadInvocationsCount());
        return () ->
                totalRemainInvs.decrementAndGet() >= 0 &&
                threadRemainInvs.decrementAndGet() >= 0 &&
                getRemainTime() >= 0;
    }


    private void beforeAll(LifeCycle<T> l, T testCase) {
        try {
            l.call(testCase);
        } catch (Throwable th) {
            log.warn("beforeAll stage: {}, testCase: {} failed", stage, testCaseName, th);
        }
    }

    private void beforeEach(Context context, T testCase) {
        log.trace("beforeEach test: {}, inv: {}", testCaseName, context.getInv());
        beforeEach.forEach(l -> {
            try {
                l.call(testCase);
            } catch (Throwable th) {
                log.warn("beforeEach test: {}, inv: {} failed. ", testCaseName, context.getInv(), th);
            }
        });
    }

    private void afterEach(Context context, T testCase) {
        log.trace("afterEach test: {}, inv: {}", context.getTest(), context.getInv());
        afterEach.forEach(l -> {
            try {
                l.call(testCase);
            } catch (Throwable th) {
                log.warn("afterEach test: {}, inv: {} failed. ", testCaseName, context.getInv(), th);
            }
        });
    }

    private void afterAll(LifeCycle<T> l, T testCase) {
        try {
            l.call(testCase);
        } catch (Throwable th) {
            log.warn("afterAll stage: {}, testCase: {} failed", stage, testCaseName, th);
        }
    }


    private void sinkAfterEach(Context context, long elapsed, Stopwatch stopwatch, @Nullable Throwable th) {
        sinks.forEach(sink -> sink.afterEach(context, elapsed, stopwatch, th));
    }


    @Override
    public String toString() {
        return "TestCaseRunner{" +
                "testCaseName='" + testCaseName + '\'' +
                '}';
    }
}
