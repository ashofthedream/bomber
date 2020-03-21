package ashes.of.bomber.runner;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.State;
import ashes.of.bomber.core.limiter.Limiter;
import ashes.of.bomber.core.stopwatch.Record;
import ashes.of.bomber.core.stopwatch.Stopwatch;
import ashes.of.bomber.sink.AsyncSink;
import ashes.of.bomber.sink.MultiSink;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.squadron.Barrier;
import ashes.of.bomber.core.stopwatch.Clock;
import ashes.of.bomber.methods.TestWithClockMethod;
import ashes.of.bomber.watchdog.Watchdog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.SECONDS;


public class Runner<T> {
    private static final Logger log = LogManager.getLogger();

    private final State state;
    private final Environment env;
    private final LifeCycle<T> lifeCycle;

    private final Sink sink;
    private final Supplier<T> testCase;

    private final Map<String, TestWithClockMethod<T>> tests;


    public Runner(State state, Environment env, LifeCycle<T> lifeCycle, Map<String, TestWithClockMethod<T>> tests, Supplier<T> testCase) {
        this.state = state;
        this.env = env;
        this.lifeCycle = lifeCycle;
        this.tests = tests;
        this.testCase = testCase;
        this.sink = new AsyncSink(new MultiSink(env.getSinks()));
    }

    public State getState() {
        return state;
    }

    /**
     * Runs the test case
     */
    public void run() {
        Settings settings = state.getSettings();
        log.info("Start stage: {}, testCase: {}, settings: {}",
                state.getStage(), state.getTestCase(), settings);

        if (settings.isDisabled()) {
            log.info("End stage: {}, testCase: {} is disabled, exit", state.getStage(), state.getTestCase());
            return;
        }

        state.startIfNotStarted();

        sink.beforeAll(state.getStage(), state.getTestCase(), state.getStartTime(), settings);

        CountDownLatch begin = new CountDownLatch(settings.getThreadsCount());
        CountDownLatch end = new CountDownLatch(settings.getThreadsCount());


        Barrier barrier = env.getBarrier()
                .workers(settings.getThreadsCount())
                .build();

        barrier.init(state.getTestCase(), settings);

        barrier.stageStart(state.getStage());


        startWatchdogThread(begin, end);
        startWorkerThreads(begin, end, barrier);

        try {
            log.info("Await for end of stage: {}, testCase: {}, elapsed {}ms",
                    state.getStage(), state.getTestCase(), state.getElapsedTime());
            end.await();
        } catch (InterruptedException e) {
            log.error("We'he been interrupted", e);
        }

        log.info("End stage: {}, testCase: {} elapsed {}ms",
                state.getStage(), state.getTestCase(), state.getElapsedTime());

        sink.afterAll(state.getStage(), state.getTestCase(), state.getStartTime(), settings);
        barrier.stageLeave(state.getStage());
    }


    private void startWatchdogThread(CountDownLatch startLatch, CountDownLatch endLatch) {
        new Watchdog(this, env.getWatchers(), startLatch, endLatch).startInNewThread();
    }

    private void startWorkerThreads(CountDownLatch startLatch, CountDownLatch endLatch, Barrier barrier) {
        for (int i = 0; i < state.getSettings().getThreadsCount(); i++)
            startWorkerThread(() -> runTestCase(startLatch, endLatch, barrier), i);
    }

    private void startWorkerThread(Runnable runnable, int index) {
        Thread thread = new Thread(runnable);
        thread.setUncaughtExceptionHandler((t, e) -> log.error("Uncaught exception in thread: {}", t.getName(), e));
        thread.setName(String.format("%s-%s-worker-%03d", state.getStage(), state.getTestCase(), index));
        thread.start();
    }


    private void runTestCase(CountDownLatch startLatch, CountDownLatch endLatch, Barrier barrier) {
        log.debug("runTestCase stage: {}, testCase: {}", state.getStage(), state.getTestCase());
        try {
            T testCase = this.testCase.get();
            Limiter limiter = env.getLimiter().get();

            startLatch.countDown();

            // if we can't start in 60 seconds – something works bad
            startLatch.await(60, SECONDS);

            lifeCycle.beforeAll(state, testCase);
            tests.forEach((name, test) -> runTest(name, testCase, test, limiter, barrier));
            lifeCycle.afterAll(state, testCase);
        } catch (Throwable th) {
            log.warn("runTestCase stage: {}, testCase: {} failed",
                    state.getStage(), state.getTestCase(), th);
        } finally {
            log.debug("runTestCase ended. stage: {}, testCase: {}", state.getStage(), state.getTestCase());
            endLatch.countDown();
        }
    }


    private void runTest(String testName, T testCase, TestWithClockMethod<T> test, Limiter limiter, Barrier barrier) {
        String threadName = Thread.currentThread().getName();
        AtomicLong invocations = new AtomicLong();
        BooleanSupplier checker = state.createChecker();
        barrier.testStart(testName);
        while (checker.getAsBoolean()) {
            if (!limiter.waitForPermit())
                throw new RuntimeException("Limiter await failed");

            long inv = invocations.getAndIncrement();
            Context context = new Context(state.getStage(), state.getTestCase(), testName, threadName, inv, Instant.now());

            log.trace("runTest stage: {}, testCase: {}, test: {}, inv: {}",
                    state.getStage(), state.getTestCase(), testName, inv);

            lifeCycle.beforeEach(context, testCase);

            Clock clock = new Clock(record -> {
                sink.onTimeRecorded(context, record);

                if (!record.isSuccess())
                    state.incError();
            });

            Stopwatch stopwatch = clock.stopwatch(context.getTest() + "." + context.getTestCase());
            try {
                // test
                test.run(testCase, clock);

                Record rec = stopwatch.success();
                sink.afterEachTest(context, rec.getElapsed(), null);
            } catch (Throwable th) {
                Record rec = stopwatch.fail(th);
                log.warn("runTest stage: {}, testCase: {}, test: {}, inv: {} failed",
                        state.getStage(), state.getTestCase(), testName, inv, th);

                sink.afterEachTest(context, rec.getElapsed(), rec.getError());
            }

            lifeCycle.afterEach(context, testCase);
        }

        barrier.testFinish(testName);
    }
}
