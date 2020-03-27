package ashes.of.bomber.builder;

import ashes.of.bomber.core.Settings;
import ashes.of.bomber.delayer.Delayer;
import ashes.of.bomber.delayer.NoDelayDelayer;
import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.methods.LifeCycleMethod;
import ashes.of.bomber.methods.TestCaseMethodWithoutTools;
import ashes.of.bomber.runner.*;
import ashes.of.bomber.methods.TestCaseMethodWithTools;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class TestSuiteBuilder<T> {

    private Settings settings = new Settings();
    private Settings warmUp = new Settings()
            .disabled();

    private final List<LifeCycleMethod<T>> beforeAll = new ArrayList<>();
    private final List<LifeCycleMethod<T>> beforeEach = new ArrayList<>();
    private final List<LifeCycleMethod<T>> afterEach = new ArrayList<>();
    private final List<LifeCycleMethod<T>> afterAll = new ArrayList<>();

    private final Map<String, TestCase<T>> testCases = new LinkedHashMap<>();
    private Delayer delayer = new NoDelayDelayer();
    private Supplier<Limiter> limiter = Limiter::alwaysPermit;
    private String name;
    private Supplier<T> testSuite;

    public TestSuiteBuilder<T> name(String name) {
        this.name = name;
        return this;
    }


    public TestSuiteBuilder<T> warmUp(Settings settings) {
        Objects.requireNonNull(settings, "settings is null");
        this.warmUp = new Settings(settings);
        return this;
    }

    public TestSuiteBuilder<T> warmUp(Consumer<Settings> settings) {
        settings.accept(warmUp);
        return this;
    }


    public TestSuiteBuilder<T> settings(Settings settings) {
        Objects.requireNonNull(settings, "settings is null");
        this.settings = new Settings(settings);
        return this;
    }

    public TestSuiteBuilder<T> settings(Consumer<Settings> settings) {
        settings.accept(this.settings);
        return this;
    }

    public TestSuiteBuilder<T> delayer(Delayer delayer) {
        Objects.requireNonNull(delayer, "delayer is null");
        this.delayer = delayer;
        return this;
    }

    /**
     * Adds limiter which will be shared across all workers threads
     *
     * @param limiter shared limiter
     * @return builder
     */
    public TestSuiteBuilder<T> limiter(Limiter limiter) {
        Objects.requireNonNull(limiter, "limiter is null");
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
        Objects.requireNonNull(object, "suite is null");
        this.testSuite = object;
        return this;
    }

    public TestSuiteBuilder<T> sharedInstance(T object) {
        Objects.requireNonNull(object, "suite is null");
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

    TestSuiteBuilder<T> testCase(String name, boolean async, TestCaseMethodWithTools<T> test) {
        Objects.requireNonNull(name, "name is null");
        this.testCases.put(name, new TestCase<>(name, async, test));
        return this;
    }

    TestSuiteBuilder<T> testCase(String name, boolean async, TestCaseMethodWithoutTools<T> test) {
        Objects.requireNonNull(name, "name is null");
        return testCase(name, async, (tc, tools) -> test.run(tc));
    }

    public TestSuiteBuilder<T> testCase(String name, TestCaseMethodWithTools<T> test) {
        return testCase(name, false, test);
    }

    public TestSuiteBuilder<T> testCase(String name, TestCaseMethodWithoutTools<T> test) {
        return testCase(name, false, test);
    }

    public TestSuiteBuilder<T> asyncTestCase(String name, TestCaseMethodWithTools<T> test) {
        return testCase(name, true, test);
    }

    public TestSuiteBuilder<T> asyncTestCase(String name, TestCaseMethodWithoutTools<T> test) {
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


    public TestSuite<T> build(WorkerPool pool, Environment app) {
        Objects.requireNonNull(name,     "name is null");
        Objects.requireNonNull(testSuite, "testCase is null");
        // todo it may be useful, but not today
        // Preconditions.checkArgument(!testCases.isEmpty(), "No test cases found");

        Environment env = new Environment(app.getSinks(), app.getWatchers(), delayer, limiter, app.getBarrier());
        LifeCycle<T> lifeCycle = new LifeCycle<>(testCases, testSuite, beforeEach, afterEach, afterAll, beforeAll);

        return new TestSuite<>(pool, name, env, lifeCycle, settings, warmUp);
    }
}

