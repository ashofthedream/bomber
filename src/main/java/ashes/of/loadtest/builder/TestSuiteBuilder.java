package ashes.of.loadtest.builder;

import ashes.of.loadtest.sink.Sink;
import ashes.of.loadtest.limiter.Limiter;
import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class TestSuiteBuilder {
    private static final Logger log = LogManager.getLogger(TestSuiteBuilder.class);

    /**
     * TestWithStopwatch cases for run
     */
    private final List<TestCaseBuilder<?>> testCases = new ArrayList<>();
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


    private <T> TestCaseBuilder<T> newTestCase() {
        return new TestCaseBuilder<T>()
                .settings(settings)
                .limiter(limiter)
                .sinks(sinks);
    }


    public <T> TestSuiteBuilder addBuilder(Consumer<TestCaseBuilder<T>> consumer) {
        TestCaseBuilder<T> b = newTestCase();
        consumer.accept(b);

        testCases.add(b);
        return this;
    }

    public <T> TestSuiteBuilder addInstance(T testCase) {
        return testCase((Class<T>) testCase.getClass(), () -> testCase);
    }

    public <T> TestSuiteBuilder addClass(Class<T> cls) {
        return testCase(cls, () -> {
            try {
                return cls.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create new instance of test case", e);
            }
        });
    }

    private <T> TestSuiteBuilder testCase(Class<T> cls, Supplier<T> supplier) {
        TestCaseBuilder<T> b = newTestCase();
        b.testCase(cls, supplier);

        testCases.add(b);
        return this;
    }


    public Runnable build() {
        Preconditions.checkArgument(!testCases.isEmpty(), "No test cases found");
        return this::run;
    }

    /**
     * Run all test cases
     */
    private void run() {
        testCases.stream()
                .map(TestCaseBuilder::build)
                .forEach(Runnable::run);
    }
}
