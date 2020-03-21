package ashes.of.bomber.builder;

import ashes.of.bomber.core.limiter.Limiter;
import ashes.of.bomber.runner.Environment;
import ashes.of.bomber.runner.TestSuite;
import ashes.of.bomber.runner.TestApp;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.squadron.BarrierBuilder;
import ashes.of.bomber.watcher.Watcher;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TestAppBuilder extends EnvironmentBuilder {

    /**
     * Test suites for run
     */
    private final List<TestSuiteBuilder<?>> suites = new ArrayList<>();
    private SettingsBuilder settings = new SettingsBuilder();

    public TestAppBuilder settings(SettingsBuilder settings) {
        this.settings = settings;
        return this;
    }

    public TestAppBuilder settings(Consumer<SettingsBuilder> consumer) {
        consumer.accept(settings);
        return this;
    }


    public TestAppBuilder barrier(BarrierBuilder barrier) {
        this.barrier = barrier;
        return this;
    }

    /**
     * @param limiter shared limiter
     * @return builder
     */
    public TestAppBuilder sharedLimiter(Limiter limiter) {
        return limiter(() -> limiter);
    }

    /**
     * @param limiter shared request limiter
     * @return builder
     */
    public TestAppBuilder limiter(Supplier<Limiter> limiter) {
        this.limiter = limiter;
        return this;
    }

    public TestAppBuilder sink(Sink sink) {
        this.sinks.add(sink);
        return this;
    }

    public TestAppBuilder sinks(List<Sink> sinks) {
        this.sinks.addAll(sinks);
        return this;
    }

    public TestAppBuilder watcher(Watcher watcher) {
        this.watchers.add(watcher);
        return this;
    }

    public TestAppBuilder watchers(List<Watcher> watcher) {
        this.watchers.addAll(watcher);
        return this;
    }

    private <T> TestSuiteBuilder<T> newTestSuite() {
        return new TestSuiteBuilder<T>()
                .barrier(barrier)
                .settings(settings)
                .limiter(limiter)
                .sinks(sinks)
                .watchers(watchers);
    }

    public <T> TestAppBuilder testSuite(TestSuiteBuilder<T> builder) {
        suites.add(builder);
        return this;
    }

    public <T> TestAppBuilder createSuite(Consumer<TestSuiteBuilder<T>> consumer) {
        TestSuiteBuilder<T> b = newTestSuite();
        consumer.accept(b);

        return testSuite(b);
    }

    public <T> TestAppBuilder testSuite(T testSuite) {
        return testSuite((Class<T>) testSuite.getClass(), () -> testSuite);
    }

    public <T> TestAppBuilder testSuiteClass(Class<T> cls, Object... args) {
        Class<?>[] types = Stream.of(args)
                .map(Object::getClass)
                .toArray(Class[]::new);

        return testSuiteClass(cls, types, args);
    }

    public <T> TestAppBuilder testSuiteClass(Class<T> cls, Class<?>[] types, Object... args) {
        return testSuite(cls, () -> {
            try {
                return cls.getConstructor(types).newInstance(args);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create new instance of test suite", e);
            }
        });
    }

    private <T> TestAppBuilder testSuite(Class<T> cls, Supplier<T> supplier) {
        TestSuiteBuilder<T> b = newTestSuite();
        b.instance(cls, supplier);

        suites.add(b);
        return this;
    }


    public TestApp build() {
        Preconditions.checkArgument(!suites.isEmpty(), "No test suites found");

        Environment env = new Environment(sinks, watchers, limiter, barrier);
        List<TestSuite<?>> suites = this.suites.stream()
                .map(TestSuiteBuilder::build)
                .collect(Collectors.toList());

        return new TestApp(env, suites);
    }
}
