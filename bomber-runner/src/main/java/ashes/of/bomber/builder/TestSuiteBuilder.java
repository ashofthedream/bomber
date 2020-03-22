package ashes.of.bomber.builder;

import ashes.of.bomber.core.Application;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.limiter.Limiter;
import ashes.of.bomber.methods.LifeCycleMethod;
import ashes.of.bomber.methods.TestCaseMethodNoArgs;
import ashes.of.bomber.runner.*;
import ashes.of.bomber.methods.TestCaseMethodWithClick;
import ashes.of.bomber.annotations.*;
import ashes.of.bomber.core.stopwatch.Clock;
import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;


public class TestSuiteBuilder<T> {
    private static final Logger log = LogManager.getLogger();

    private final TestAppBuilder app;

    private final List<LifeCycleMethod<T>> beforeAll = new ArrayList<>();
    private final List<LifeCycleMethod<T>> beforeEach = new ArrayList<>();
    private final List<LifeCycleMethod<T>> afterEach = new ArrayList<>();
    private final List<LifeCycleMethod<T>> afterAll = new ArrayList<>();

    private final Map<String, TestCase<T>> testCases = new LinkedHashMap<>();
    private final TestCase<T> noop = new TestCase<>("noop", false, (test, stopwatch) -> {});

    private Supplier<Limiter> limiter;
    private String name;
    private Supplier<T> testSuite;
    private SettingsBuilder settings;

    public TestSuiteBuilder(TestAppBuilder app) {
        this.app = app;
        this.limiter = app.limiter();
        this.settings = new SettingsBuilder(app.settings());
    }

    public TestSuiteBuilder() {
        this(new TestAppBuilder());
    }

    public TestSuiteBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    // todo used only for test reasons
    @Deprecated
    public TestSuiteBuilder<T> app(Consumer<TestAppBuilder> consumer) {
        consumer.accept(app);
        return this;
    }

    private SettingsBuilder settings() {
        return settings;
    }

    public TestSuiteBuilder<T> settings(Consumer<SettingsBuilder> builder) {
        builder.accept(settings);
        return this;
    }

    public TestSuiteBuilder<T> settings(SettingsBuilder settings) {
        this.settings = new SettingsBuilder(settings);
        return this;
    }


    /**
     * Adds limiter which will be shared across all workers threads
     *
     * @param limiter shared limiter
     * @return builder
     */
    public TestSuiteBuilder<T> limiter(Limiter limiter) {
        return limiter(() -> limiter);
    }

    /**
     * Adds limiter which will be created for each worker thread
     * note: it may be shared if supplier will return same instance
     *
     * @param limiter shared request limiter
     * @return builder
     */
    public TestSuiteBuilder<T> limiter(Supplier<Limiter> limiter) {
        this.limiter = limiter;
        return this;
    }


    public TestSuiteBuilder<T> instance(Supplier<T> object) {
        Preconditions.checkNotNull(object, "object is null");
        this.testSuite = object;
        return this;
    }

    public TestSuiteBuilder<T> sharedInstance(T object) {
        Preconditions.checkNotNull(object, "suite is null");
        return instance(() -> object);
    }


    public TestSuiteBuilder<T> beforeAll(boolean onlyOnce, LifeCycleMethod<T> before) {
        beforeAll.add(onlyOnce ? onlyOnce(before) : before);
        return this;
    }

    public TestSuiteBuilder<T> beforeAll(LifeCycleMethod<T> before) {
        return beforeAll(false, before);
    }


    /**
     * Adds method that will be invoked before each test invocation
     *
     * @param before method reference
     * @return builder
     */
    public TestSuiteBuilder<T> beforeEach(LifeCycleMethod<T> before) {
        beforeEach.add(before);
        return this;
    }

    private TestSuiteBuilder<T> testCase(String name, boolean async, TestCaseMethodWithClick<T> test) {
        this.testCases.put(name, new TestCase<>(name, async, test));
        return this;
    }

    private TestSuiteBuilder<T> testCase(String name, boolean async, TestCaseMethodNoArgs<T> test) {
        return testCase(name, async, (tc, stopwatch) -> test.run(tc));
    }


    public TestSuiteBuilder<T> testCase(String name, TestCaseMethodWithClick<T> test) {
        return testCase(name, false, test);
    }

    public TestSuiteBuilder<T> testCase(String name, TestCaseMethodNoArgs<T> test) {
        return testCase(name, false, test);
    }

    public TestSuiteBuilder<T> asyncTestCase(String name, TestCaseMethodWithClick<T> test) {
        return testCase(name, true, test);
    }

    public TestSuiteBuilder<T> asyncTestCase(String name, TestCaseMethodNoArgs<T> test) {
        return testCase(name, true, test);
    }


    /**
     * Adds method that will be invoked after each test invocation
     *
     * @param after method reference
     * @return builder
     */
    public TestSuiteBuilder<T> afterEach(LifeCycleMethod<T> after) {
        afterEach.add(after);
        return this;
    }


    public TestSuiteBuilder<T> afterAll(boolean onlyOnce, LifeCycleMethod<T> after) {
        afterAll.add(onlyOnce ? onlyOnce(after) : after);
        return this;
    }

    public TestSuiteBuilder<T> afterAll(LifeCycleMethod<T> after) {
        return afterAll(false, after);
    }


    private LifeCycleMethod<T> onlyOnce(LifeCycleMethod<T> method) {
        AtomicBoolean onlyOnceCheck = new AtomicBoolean();
        return tc -> {
            // first thread initializes the content, all other - wait for initialization
            synchronized (onlyOnceCheck) {
                if (onlyOnceCheck.compareAndSet(false, true))
                    method.call(tc);
            }
        };
    }


    public TestSuiteBuilder<T> instance(Class<T> cls, Supplier<T> supplier) {
        Baseline baseline = cls.getAnnotation(Baseline.class);
        if (baseline != null)
            settings.baseline(makeSettings(baseline));

        WarmUp warmUp = cls.getAnnotation(WarmUp.class);
        if (warmUp != null)
            settings.warmUp(makeSettings(warmUp));


        LoadTestSuite test = cls.getAnnotation(LoadTestSuite.class);
        if (test != null) {
            name(!test.name().isEmpty() ? test.name() : cls.getSimpleName());

            settings.test(makeSettings(test));

            if (test.shared()) {
                sharedInstance(supplier.get());
            } else {
                instance(supplier);
            }

        } else {
            name(cls.getSimpleName());
        }

        Throttle limit = cls.getAnnotation(Throttle.class);
        if (limit != null) {
            Supplier<Limiter> limiter = () -> Limiter.withRate(limit.threshold(), limit.time(), limit.timeUnit());
            if (limit.shared()) {
                limiter(limiter.get());
            } else {
                limiter(limiter);
            }
        }


        for (Method method : cls.getDeclaredMethods()) {
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) {
                continue;
            }

            log.debug("check cls: {} method: {}", cls.getClass(), method.getName());
            try {
                BeforeAll beforeAll = method.getAnnotation(BeforeAll.class);
                if (beforeAll != null)
                    buildBeforeAll(method, beforeAll);

                BeforeEach beforeEach = method.getAnnotation(BeforeEach.class);
                if (beforeEach != null)
                    buildBeforeEach(method, beforeEach);

                LoadTestCase loadTest = method.getAnnotation(LoadTestCase.class);
                if (loadTest != null)
                    buildTest(method, loadTest);

                AfterEach afterEach = method.getAnnotation(AfterEach.class);
                if (afterEach != null)
                    buildAfterEach(method, afterEach);

                AfterAll afterAll = method.getAnnotation(AfterAll.class);
                if (afterAll != null)
                    buildAfterAll(method, afterAll);

            } catch (Exception e) {
                log.warn("Can't mh method: {}", method.getName(), e);
            }
        }

        return this;
    }


    private Settings makeSettings(LoadTestSuite ann) {
        return new Settings()
                .threadCount(ann.threads())
                .threadInvocationCount(ann.threadInvocations())
                .totalInvocationCount(ann.totalInvocations())
                .time(ann.time(), ann.timeUnit());
    }

    private Settings makeSettings(WarmUp ann) {
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


    private void buildBeforeAll(Method method, BeforeAll beforeAll) throws Exception {
        log.debug("Found beforeAll method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        beforeAll(beforeAll.onlyOnce(), testCase -> mh.bindTo(testCase).invoke());
    }

    private void buildBeforeEach(Method method, BeforeEach beforeEach) throws Exception {
        log.debug("Found beforeEach method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        beforeEach(testCase -> mh.bindTo(testCase).invoke());
    }

    private void buildTest(Method method, LoadTestCase loadTest) throws Exception {
        String value = loadTest.value();
        String name = !value.isEmpty() ? value : method.getName();
        log.debug("Found test method: {}, name: {}, disabled: {}", method.getName(), name, loadTest.disabled());

        if (loadTest.disabled())
            return;

        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        AtomicReference<TestCaseMethodWithClick<T>> ref = new AtomicReference<>();

        testCase(name, loadTest.async(), (suite, clock) -> {
            TestCaseMethodWithClick<T> proxy = ref.get();
            if (proxy == null) {
                log.warn("init testCase: {} proxy method", name);
                Class<?>[] types = method.getParameterTypes();
                Object[] params = Stream.of(types)
                        .map(param -> {
                            if (param.equals(Clock.class))
                                return clock;

                            throw new RuntimeException("Skip test " + name + ": not allowed parameters (only Stopwatch is allowed)");
                        })
                        .toArray();

                log.trace("bind test method with params: {}", Arrays.toString(params));

                MethodHandle bind = mh.bindTo(suite);

                proxy = types.length == 0 ?
                        (tc, sw) -> bind.invoke() :
                        (tc, sw) -> bind.invokeWithArguments(sw) ;

                ref.set(proxy);
            }

            proxy.run(suite, clock);
        });
    }

    private void buildAfterEach(Method method, AfterEach afterEach) throws Exception {
        log.debug("Found afterEach method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        afterEach(testCase -> mh.bindTo(testCase).invoke());
    }

    private void buildAfterAll(Method method, AfterAll afterAll) throws Exception {
        log.debug("Found afterAll method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        afterAll(afterAll.onlyOnce(), testCase -> mh.bindTo(testCase).invoke());
    }

    public Application application() {
        return this.app.addSuite(this)
                .application();
    }

    public TestSuite<T> build(Environment appEnv) {
        Preconditions.checkNotNull(name,     "name is null");
        Preconditions.checkNotNull(testSuite, "testCase is null");
        Preconditions.checkNotNull(appEnv, "env is null");

        Environment env = new Environment(appEnv.getSinks(), appEnv.getWatchers(), limiter, appEnv.getBarrier());
        LifeCycle<T> lifeCycle = new LifeCycle<>(testCases, testSuite, beforeEach, afterEach, afterAll, beforeAll);

        return new TestSuite<>(name, env, lifeCycle, settings.getWarmUp(), settings.getTest());
    }
}

