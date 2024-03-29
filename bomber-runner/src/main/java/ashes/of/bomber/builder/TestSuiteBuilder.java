package ashes.of.bomber.builder;

import ashes.of.bomber.methods.LifeCycleHolder;
import ashes.of.bomber.methods.LifeCycleMethod;
import ashes.of.bomber.methods.TestCaseMethod;
import ashes.of.bomber.core.TestCase;
import ashes.of.bomber.core.TestSuite;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class TestSuiteBuilder<T> {

    private final List<LifeCycleHolder<T>> beforeSuite = new ArrayList<>();
    private final List<LifeCycleHolder<T>> beforeCase = new ArrayList<>();
    private final List<LifeCycleHolder<T>> beforeEach = new ArrayList<>();
    private final Map<String, TestCase<T>> testCases = new LinkedHashMap<>();
    private final List<LifeCycleHolder<T>> afterEach = new ArrayList<>();
    private final List<LifeCycleHolder<T>> afterCase = new ArrayList<>();
    private final List<LifeCycleHolder<T>> afterSuite = new ArrayList<>();

    private String name;
    private ConfigurationBuilder config = new ConfigurationBuilder();
    private Supplier<T> context = () -> null;

    public TestSuiteBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    public TestSuiteBuilder<T> config(Consumer<ConfigurationBuilder> consumer) {
        consumer.accept(this.config);
        return this;
    }

    public TestSuiteBuilder<T> config(ConfigurationBuilder config) {
        this.config = config;
        return this;
    }

    /**
     * Creates context on each worker
     *
     * note: context should be thread safe
     */
    public TestSuiteBuilder<T> createContext(Supplier<T> context) {
        Objects.requireNonNull(context, "context is null");
        this.context = context;
        return this;
    }

    /**
     * Uses context for all workers
     */
    public TestSuiteBuilder<T> withContext(T context) {
        Objects.requireNonNull(context, "suite is null");
        return createContext(() -> context);
    }



    public TestSuiteBuilder<T> beforeSuite(boolean onlyOnce, LifeCycleMethod<T> before) {
        beforeSuite.add(new LifeCycleHolder<>(onlyOnce, before));
        return this;
    }

    public TestSuiteBuilder<T> beforeSuite(LifeCycleMethod<T> before) {
        return beforeSuite(false, before);
    }

    
    public TestSuiteBuilder<T> beforeCase(boolean onlyOnce, LifeCycleMethod<T> before) {
        beforeCase.add(new LifeCycleHolder<>(onlyOnce, before));
        return this;
    }

    public TestSuiteBuilder<T> beforeCase(LifeCycleMethod<T> before) {
        return beforeCase(false, before);
    }

    /**
     * Adds method that will be invoked before each test invocation
     *
     * @param before method reference
     * @return builder
     */
    public TestSuiteBuilder<T> beforeEach(LifeCycleMethod<T> before) {
        beforeEach.add(new LifeCycleHolder<>(false, before));
        return this;
    }


    private TestCaseBuilder<T> newTestCaseBuilder() {
        return new TestCaseBuilder<T>()
                .config(new ConfigurationBuilder(config));
    }

    public TestSuiteBuilder<T> testCase(Consumer<TestCaseBuilder<T>> consumer) {
        var b = newTestCaseBuilder();

        consumer.accept(b);
        return testCase(b);
    }

    public TestSuiteBuilder<T> testCase(TestCaseBuilder<T> builder) {
        Objects.requireNonNull(builder, "builder is null");
        var testCase = builder.build();
        this.testCases.put(testCase.getName(), testCase);
        return this;
    }

    public TestSuiteBuilder<T> testCase(String name, boolean async, TestCaseMethod<T> test) {
        Objects.requireNonNull(name, "name is null");
        Objects.requireNonNull(test, "test is null");

        return testCase(newTestCaseBuilder()
                .name(name)
                .async(async)
                .test(test));
    }

    public TestSuiteBuilder<T> testCase(String name, TestCaseMethod<T> test) {
        return testCase(name, false, test);
    }

    public TestSuiteBuilder<T> asyncTestCase(String name, TestCaseMethod<T> test) {
        return testCase(name, true, test);
    }

    /**
     * Adds method that will be invoked after each test invocation
     *
     * @param after method reference
     * @return builder
     */
    public TestSuiteBuilder<T> afterEach(LifeCycleMethod<T> after) {
        afterEach.add(new LifeCycleHolder<>(false, after));
        return this;
    }

    public TestSuiteBuilder<T> afterCase(boolean onlyOnce, LifeCycleMethod<T> after) {
        afterCase.add(new LifeCycleHolder<>(onlyOnce, after));
        return this;
    }

    public TestSuiteBuilder<T> afterCase(LifeCycleMethod<T> after) {
        return afterCase(false, after);
    }
    
    public TestSuiteBuilder<T> afterSuite(boolean onlyOnce, LifeCycleMethod<T> after) {
        afterSuite.add(new LifeCycleHolder<>(onlyOnce, after));
        return this;
    }

    public TestSuiteBuilder<T> afterSuite(LifeCycleMethod<T> after) {
        return afterSuite(false, after);
    }


    public boolean hasTestCases() {
        return !testCases.isEmpty();
    }

    public TestSuite<T> build() {
        Objects.requireNonNull(name,     "name is null");
        // todo it may be useful, but not today
        Preconditions.checkArgument(!testCases.isEmpty(), "No test cases found for test suite: " + name);

        return new TestSuite<>(name, context, beforeSuite, beforeCase, beforeEach, testCases, afterEach, afterCase, afterSuite);
    }
}

