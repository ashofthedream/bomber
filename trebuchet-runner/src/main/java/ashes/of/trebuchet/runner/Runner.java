package ashes.of.trebuchet.runner;

import ashes.of.trebuchet.builder.Settings;
import ashes.of.trebuchet.distibuted.Barrier;
import ashes.of.trebuchet.distibuted.BarrierBuilder;
import ashes.of.trebuchet.limiter.Limiter;
import ashes.of.trebuchet.sink.Sink;
import ashes.of.trebuchet.stopwatch.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.SECONDS;


public class Runner<T> {
    private static final Logger log = LogManager.getLogger();

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
    private final Map<String, Test<T>> tests;
    private final Supplier<Limiter> limiter;
    private final BarrierBuilder barrier;

    public Runner(String testCaseName,
                  Stage stage,
                  Settings settings,
                  List<Sink> sinks,
                  List<LifeCycle<T>> beforeAll,
                  List<LifeCycle<T>> beforeEach,
                  Map<String, Test<T>> tests,
                  Supplier<T> testCase,
                  List<LifeCycle<T>> afterEach,
                  List<LifeCycle<T>> afterAll,
                  Supplier<Limiter> limiter,
                  BarrierBuilder barrier) {
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
        this.limiter = limiter;
        this.barrier = barrier;
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

    public long getRemainTime() {
        return getRemainTime(startTime);
    }

    private long getRemainTime(Instant startTime) {
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
            log.info("Ignore disabled {}", logMeta());
            return;
        }

        log.info("Start {}, threads: {}, iterations total: {} per thread: {}, duration: {}",
                logMeta(), settings.getThreadsCount(), settings.getTotalInvocationsCount(), settings.getThreadInvocationsCount(), settings.getTime());

        sinks.forEach(sink -> sink.beforeAll(stage, testCaseName, startTime, settings));

        CountDownLatch begin = new CountDownLatch(settings.getThreadsCount());
        CountDownLatch end = new CountDownLatch(settings.getThreadsCount());

        Barrier b = barrier.workers(settings.getThreadsCount()).build();
        startTime = Instant.now();
        startWatchdogThread(begin, end);
        startWorkerThreads(begin, end, b);

        try {
            log.info("Await for end of {}, elapsed {}ms", logMeta(), getElapsedTime());
            end.await();
        } catch (InterruptedException e) {
            log.error("We'he been interrupted", e);
        }

        log.info("End {} elapsed {}ms", logMeta(), getElapsedTime());
        sinks.forEach(sink -> sink.afterAll(stage, testCaseName, startTime, settings));
    }


    private void startWatchdogThread(CountDownLatch startLatch, CountDownLatch endLatch) {
        new Watchdog(this, startLatch, endLatch).startInNewThread();
    }

    private void startWorkerThreads(CountDownLatch startLatch, CountDownLatch endLatch, Barrier barrier) {
        for (int i = 0; i < settings.getThreadsCount(); i++)
            startWorkerThread(() -> runTestCase(startLatch, endLatch, barrier), i);
    }

    private void startWorkerThread(Runnable runnable, int index) {
        Thread thread = new Thread(runnable);
        thread.setName(String.format("%s-%s-worker-%03d", testCaseName, stage.name(), index));
        thread.start();
    }


    private void runTestCase(CountDownLatch startLatch, CountDownLatch endLatch, Barrier barrier) {
        try {
            T testCase = this.testCase.get();
            Limiter limiter = this.limiter.get();

            startLatch.countDown();

            // if we can't start in 60 seconds – something works bad
            startLatch.await(60, SECONDS);

            log.trace("beforeAll {}", this::logMeta);
            beforeAll.forEach(l -> beforeAll(l, testCase));

            tests.forEach((name, test) -> runTest(name, testCase, test, limiter, barrier));

            log.trace("afterAll {}", this::logMeta);
            afterAll.forEach(l -> afterAll(l, testCase));
        } catch (Throwable e) {
            log.warn("runTestCase {} failed", logMeta(), e);
        } finally {
            endLatch.countDown();
        }
    }


    private void runTest(String testName, T testCase, Test<T> test, Limiter limiter, Barrier barrier) {
        String threadName = Thread.currentThread().getName();
        AtomicLong invocations = new AtomicLong();
        BooleanSupplier checker = checker();
        barrier.enter(testName);
        while (checker.getAsBoolean()) {
            if (!limiter.waitForAcquire())
                throw new RuntimeException("Limiter await failed");

            long inv = invocations.getAndIncrement();
            Context ctx = new Context(stage, testCaseName, testName, threadName, inv, Instant.now());

            log.trace("runTest {}", logMeta(ctx));
            beforeEach(ctx, testCase);

            Stopwatch stopwatch = new Stopwatch();
            try {
                // test
                test.run(testCase, stopwatch);

                sinkAfterEach(ctx, stopwatch.elapsed(), stopwatch, null);
            } catch (Throwable th) {
                log.debug("runTest {} failed", logMeta(ctx), th);
                errorCount.increment();
                sinkAfterEach(ctx, stopwatch.elapsed(), stopwatch, th);
            }

            afterEach(ctx, testCase);
        }

        barrier.leave(testName);
    }

    private BooleanSupplier checker() {
        Instant startTime = Instant.now();
        AtomicLong threadRemainInvs = new AtomicLong(settings.getThreadInvocationsCount());
        return () ->
                totalRemainInvs.decrementAndGet() >= 0 &&
                threadRemainInvs.decrementAndGet() >= 0 &&
                getRemainTime(startTime) >= 0;
    }


    private void beforeAll(LifeCycle<T> l, T testCase) {
        try {
            l.call(testCase);
        } catch (Throwable th) {
            log.warn("beforeAll {} failed", logMeta(), th);
        }
    }

    private void beforeEach(Context context, T testCase) {
        beforeEach.forEach(l -> {
            try {
                l.call(testCase);
            } catch (Throwable th) {
                log.warn("beforeEach {} failed.", logMeta(context), th);
            }
        });
    }

    private void afterEach(Context context, T testCase) {
        afterEach.forEach(l -> {
            try {
                l.call(testCase);
            } catch (Throwable th) {
                log.warn("afterEach {} failed.", logMeta(context), th);
            }
        });
    }

    private void afterAll(LifeCycle<T> l, T testCase) {
        try {
            l.call(testCase);
        } catch (Throwable th) {
            log.warn("afterAll {} failed", logMeta(), th);
        }
    }


    private void sinkAfterEach(Context context, long elapsed, Stopwatch stopwatch, @Nullable Throwable th) {
        sinks.forEach(sink -> sink.afterEach(context, elapsed, stopwatch, th));
    }

    private String logMeta() {
        return String.format("stage: %s, testCase: %s", stage, testCaseName);
    }

    private String logMeta(Context ctx) {
        return String.format("stage: %s, testCase: %s, test: %s, inv: %d", stage, testCaseName, ctx.getTest(), ctx.getInv());
    }

    @Override
    public String toString() {
        return "Runner{" +
                "testCaseName='" + testCaseName + '\'' +
                '}';
    }
}
