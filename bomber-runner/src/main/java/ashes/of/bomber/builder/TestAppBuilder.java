package ashes.of.bomber.builder;

import ashes.of.bomber.core.BomberApp;
import ashes.of.bomber.core.limiter.Limiter;
import ashes.of.bomber.runner.Environment;
import ashes.of.bomber.runner.TestSuite;
import ashes.of.bomber.runner.TestApp;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.squadron.BarrierBuilder;
import ashes.of.bomber.squadron.NoBarrier;
import ashes.of.bomber.watcher.Watcher;
import ashes.of.bomber.watcher.WatcherConfig;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TestAppBuilder {

    private final List<Sink> sinks = new ArrayList<>();
    private final List<WatcherConfig> watchers = new ArrayList<>();
    private SettingsBuilder settings = new SettingsBuilder();
    private BarrierBuilder barrier = new NoBarrier.Builder();
    private Supplier<Limiter> limiter = Limiter::alwaysPermit;

    /**
     * Test suites for run
     */
    private final List<TestSuiteBuilder<?>> suites = new ArrayList<>();


    public TestAppBuilder settings(SettingsBuilder settings) {
        this.settings = settings;
        return this;
    }

    public TestAppBuilder settings(Consumer<SettingsBuilder> consumer) {
        consumer.accept(settings);
        return this;
    }

    public SettingsBuilder settings() {
        return this.settings;
    }

    public TestAppBuilder barrier(BarrierBuilder barrier) {
        this.barrier = barrier;
        return this;
    }


    /**
     * Adds limiter which will be shared across all workers threads
     *
     * @param limiter shared limiter
     * @return builder
     */
    public TestAppBuilder limiter(Limiter limiter) {
        return limiter(() -> limiter);
    }

    /**
     * Adds limiter which will be created for each worker thread
     * note: it may be shared if supplier will return same instance
     *
     * @param limiter shared request limiter
     * @return builder
     */
    public TestAppBuilder limiter(Supplier<Limiter> limiter) {
        this.limiter = limiter;
        return this;
    }

    public Supplier<Limiter> limiter() {
        return limiter;
    }


    public TestAppBuilder sink(Sink sink) {
        this.sinks.add(sink);
        return this;
    }

    public TestAppBuilder sinks(List<Sink> sinks) {
        this.sinks.addAll(sinks);
        return this;
    }


    public TestAppBuilder watcher(long period, TimeUnit unit, Watcher watcher) {
        this.watchers.add(new WatcherConfig(period, unit, watcher));
        return this;
    }

    public TestAppBuilder watcher(long ms, Watcher watcher) {
        return watcher(ms, TimeUnit.MILLISECONDS, watcher);
    }

    public TestAppBuilder watcher(Watcher watcher) {
        return watcher(1000, watcher);
    }

    public TestAppBuilder watchers(long period, TimeUnit unit, List<Watcher> watchers) {
        watchers.forEach(watcher -> watcher(period, unit, watcher));
        return this;
    }

    public TestAppBuilder watchers(long ms, List<Watcher> watchers) {
        return watchers(ms, TimeUnit.MILLISECONDS, watchers);
    }

    public TestAppBuilder watchers(List<Watcher> watchers) {
        return watchers(1000, watchers);
    }


    public <T> TestAppBuilder addSuite(TestSuiteBuilder<T> builder) {
        suites.add(builder);
        return this;
    }

    public <T> TestAppBuilder createSuite(Consumer<TestSuiteBuilder<T>> consumer) {
        TestSuiteBuilder<T> b = new TestSuiteBuilder<T>(this);
        consumer.accept(b);

        return addSuite(b);
    }

    public <T, C> TestAppBuilder createSuite(BiConsumer<TestSuiteBuilder<T>, C> consumer, C context) {
        TestSuiteBuilder<T> b = new TestSuiteBuilder<T>(this);
        consumer.accept(b, context);

        return addSuite(b);
    }

    public <T> TestAppBuilder testSuiteObject(T testSuite) {
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
        TestSuiteBuilder<T> builder = new TestSuiteBuilder<T>(this);
        builder.instance(cls, supplier);

        return addSuite(builder);
    }

    public BomberApp build() {
        Preconditions.checkArgument(!suites.isEmpty(), "No test suites found");

        Environment env = new Environment(sinks, watchers, limiter, barrier);
        List<TestSuite<?>> suites = this.suites.stream()
                .map(b -> b.build(env))
                .collect(Collectors.toList());

        return new TestApp(env, suites);
    }
}
