package ashes.of.trebuchet.builder;

import ashes.of.bomber.core.limiter.Limiter;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.squadron.BarrierBuilder;
import ashes.of.bomber.squadron.NoBarrier;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class TestSuiteBuilder {

    /**
     * Test cases for run
     */
    private final List<TestCaseBuilder<?>> testCases = new ArrayList<>();
    private final List<Sink> sinks = new ArrayList<>();
    private final SettingsBuilder settings = new SettingsBuilder();

    private Supplier<Limiter> limiter = Limiter::alwaysPermit;
    private BarrierBuilder barrier = new NoBarrier.Builder();

    public TestSuiteBuilder settings(Consumer<SettingsBuilder> consumer) {
        consumer.accept(settings);
        return this;
    }


    public TestSuiteBuilder barrier(BarrierBuilder barrier) {
        this.barrier = barrier;
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
                .barrier(barrier)
                .settings(settings)
                .limiter(limiter)
                .sinks(sinks);
    }

    public <T> TestSuiteBuilder addBuilder(TestCaseBuilder<T> builder) {
        testCases.add(builder);
        return this;
    }

    public <T> TestSuiteBuilder addBuilder(Consumer<TestCaseBuilder<T>> consumer) {
        TestCaseBuilder<T> b = newTestCase();
        consumer.accept(b);

        return addBuilder(b);
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
