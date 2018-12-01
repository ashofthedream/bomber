package ashes.of.loadtest.builder;

import ashes.of.loadtest.TestCase;
import ashes.of.loadtest.annotations.*;
import ashes.of.loadtest.settings.Settings;
import ashes.of.loadtest.sink.Sink;
import ashes.of.loadtest.stopwatch.Stopwatch;
import ashes.of.loadtest.throttler.Limiter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;


public class TestSuiteBuilder {
    private static final Logger log = LogManager.getLogger(TestSuiteBuilder.class);

    /**
     * TestWithStopwatch cases for run
     */
    private final List<TestCaseBuilder<? extends TestCase>> testCases = new ArrayList<>();
    private final List<Sink> sinks = new ArrayList<>();
    private final SettingsBuilder settings = new SettingsBuilder();

    private Supplier<Limiter> limiter = Limiter::alwaysPermit;


    public TestSuiteBuilder settings(Consumer<SettingsBuilder> consumer) {
        consumer.accept(settings);
        return this;
    }

    /**
     * @param limiter shared limiter
     * @return builder
     */
    public TestSuiteBuilder limiter(Limiter limiter) {
        return limiter(() -> limiter);
    }

    /**
     * @param limiter shared request limiter
     * @return builder
     */
    public TestSuiteBuilder limiter(Supplier<Limiter> limiter) {
        this.limiter = limiter;
        return this;
    }

    public TestSuiteBuilder sink(Sink sink) {
        this.sinks.add(sink);
        return this;
    }

    public TestSuiteBuilder sinks(List<Sink> sinks) {
        this.sinks.addAll(sinks);
        return this;
    }


    private <T extends TestCase> TestCaseBuilder<T> newTestCase() {
        return new TestCaseBuilder<T>()
                .settings(settings)
                .limiter(limiter)
                .sinks(sinks);
    }

    public <T extends TestCase> TestSuiteBuilder testBuilder(Consumer<TestCaseBuilder<T>> consumer) {
        TestCaseBuilder<T> b = newTestCase();
        consumer.accept(b);

        testCases.add(b);
        return this;
    }

    public <T extends TestCase> TestSuiteBuilder testInstance(T testCase) {
        return testCase((Class<T>) testCase.getClass(), () -> testCase);
    }

    public <T extends TestCase> TestSuiteBuilder testClass(Class<T> cls) {
        return testCase(cls, () -> {
            try {
                return cls.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create new instance of test case", e);
            }
        });
    }

    private <T extends TestCase> TestSuiteBuilder testCase(Class<T> cls, Supplier<T> supplier) {
        TestCaseBuilder<T> b = newTestCase();

        Baseline baseline = cls.getAnnotation(Baseline.class);
        if (baseline != null)
            b.settings().baseline(makeSettings(baseline));

        Warmup warmUp = cls.getAnnotation(Warmup.class);
        if (warmUp != null)
            b.settings().warmUp(makeSettings(warmUp));


        LoadTestCase test = cls.getAnnotation(LoadTestCase.class);
        if (test != null) {
            b       .name(!test.name().isEmpty() ? test.name() : cls.getSimpleName())
                    .settings()
                            .test(makeSettings(test));


            if (test.concurrent()) {
                b.testCase(supplier.get());
            } else {
                b.testCase(supplier);
            }

        } else {
            b.name(cls.getSimpleName());
        }

        Limit limit = cls.getAnnotation(Limit.class);
        if (limit != null)
            b.limiter(Limiter.withRate(limit.count(), limit.time(), limit.timeUnit()));


        for (Method method : cls.getDeclaredMethods()) {
            try {
                BeforeAll beforeAll = method.getAnnotation(BeforeAll.class);
                if (beforeAll != null)
                    buildBeforeAll(b, method, beforeAll);

                BeforeEach beforeEach = method.getAnnotation(BeforeEach.class);
                if (beforeEach != null)
                    buildBeforeEach(b, method, beforeEach);

                LoadTest loadTest = method.getAnnotation(LoadTest.class);
                if (loadTest != null)
                    buildTest(b, method, loadTest);

                AfterEach afterEach = method.getAnnotation(AfterEach.class);
                if (afterEach != null)
                    buildAfterEach(b, method, afterEach);

                AfterAll afterAll = method.getAnnotation(AfterAll.class);
                if (afterAll != null)
                    buildAfterAll(b, method, afterAll);

            } catch (Exception e) {
                log.warn("Can't mh method: {}", method.getName(), e);
            }
        }

        testCases.add(b);
        return this;
    }


    private Settings makeSettings(LoadTestCase ann) {
        return new Settings()
                .threadCount(ann.threads())
                .threadInvocationCount(ann.threadInvocations())
                .totalInvocationCount(ann.totalInvocations())
                .time(ann.time(), ann.timeUnit());
    }

    private Settings makeSettings(Warmup ann) {
        return new Settings()
                .threadCount(ann.threads())
                .threadInvocationCount(ann.threadInvocations())
                .totalInvocationCount(ann.totalInvocations())
                .time(ann.time(), ann.timeUnit())
                .disabled(ann.disabled());
    }

    private Settings makeSettings(Baseline ann) {
        return new Settings()
                .time(ann.time(), ann.timeUnit())
                .disabled(ann.disabled());
    }


    private <T extends TestCase> void buildBeforeAll(TestCaseBuilder<T> b, Method method, BeforeAll beforeAll) throws Exception {
        log.debug("Found beforeAll method: {}", method.getName());
        boolean onlyOnce = beforeAll.onlyOnce();
        AtomicBoolean onlyOnceCheck = new AtomicBoolean();

        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        b.beforeAll(testCase -> {

            if (!onlyOnceCheck.compareAndSet(false, onlyOnce))
                return;

            mh.bindTo(testCase).invoke();
        });
    }

    private <T extends TestCase> void buildBeforeEach(TestCaseBuilder<T> b, Method method, BeforeEach beforeEach) throws Exception {
        log.debug("Found beforeEach method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        b.beforeEach(testCase ->
                mh.bindTo(testCase).invoke());
    }

    private <T extends TestCase> void buildTest(TestCaseBuilder<T> b, Method method, LoadTest loadTest) throws Exception {
        String value = loadTest.value();
        String name = !value.isEmpty() ? value : method.getName();
        log.debug("Found test method: {}, name: {}", method.getName(), name);
        MethodHandle mh = MethodHandles.lookup().unreflect(method);

        b.test(name, (testCase, stopwatch) -> {
            Class<?>[] types = method.getParameterTypes();
            Object[] params = Stream.of(types)
                    .map(param -> {
                        if (param.equals(Stopwatch.class))
                            return stopwatch;

                        throw new RuntimeException("Skip test " + name + ": not allowed parameters (only Stopwatch is allowed)");
                    })
                    .toArray();

            log.trace("bind test method with params: {}", Arrays.toString(params));

            if (params.length == 0)
                mh.bindTo(testCase).invoke();
            else
                mh.bindTo(testCase).invokeWithArguments(params);
        });
    }

    private <T extends TestCase> void buildAfterEach(TestCaseBuilder<T> b, Method method, AfterEach afterEach) throws Exception {
        log.debug("Found afterEach method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        b.afterEach(testCase ->
                mh.bindTo(testCase).invoke());
    }

    private <T extends TestCase> void buildAfterAll(TestCaseBuilder<T> b, Method method, AfterAll afterAll) throws Exception {
        log.debug("Found afterAll method: {}", method.getName());
        boolean onlyOnce = afterAll.onlyOnce();
        AtomicBoolean onlyOnceCheck = new AtomicBoolean();
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        b.afterAll(testCase -> {
            if (!onlyOnceCheck.compareAndSet(false, onlyOnce))
                return;

            mh.bindTo(testCase).invoke();
        });
    }


    /**
     * Run all test cases
     */
    public void run() {
        testCases.stream()
                .map(TestCaseBuilder::build)
                .forEach(Runnable::run);
    }
}
