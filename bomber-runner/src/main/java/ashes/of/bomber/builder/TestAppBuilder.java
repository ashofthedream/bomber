package ashes.of.bomber.builder;

import ashes.of.bomber.core.BomberApp;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.delayer.Delayer;
import ashes.of.bomber.delayer.NoDelayDelayer;
import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.runner.Environment;
import ashes.of.bomber.runner.TestApp;
import ashes.of.bomber.runner.TestSuite;
import ashes.of.bomber.runner.WorkerPool;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.squadron.BarrierBuilder;
import ashes.of.bomber.squadron.NoBarrier;
import ashes.of.bomber.watcher.Watcher;
import ashes.of.bomber.watcher.WatcherConfig;
import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.TriConsumer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TestAppBuilder {
    private static final Logger log = LogManager.getLogger();

    private String name;

    private Settings warmUp = new Settings()
            .disabled();

    private Settings settings = new Settings();

    private final List<Sink> sinks = new ArrayList<>();
    private final List<WatcherConfig> watchers = new ArrayList<>();
    private BarrierBuilder barrier = new NoBarrier.Builder();
    private Delayer delayer = new NoDelayDelayer();
    private Supplier<Limiter> limiter = Limiter::alwaysPermit;

    private final ProviderBuilder provider = new ProviderBuilder();

    /**
     * Test suites for run
     */
    private final List<TestSuiteBuilder<?>> suites = new ArrayList<>();


    public static TestAppBuilder create(Class<?> cls) {
        Objects.requireNonNull(cls, "cls is null");
        return new TestAppProcessor()
                .process(cls);
    }

    public TestAppBuilder name(String name) {
        Objects.requireNonNull(name, "name is null");
        this.name = name;
        return this;
    }


    public TestAppBuilder warmUp(Settings settings) {
        Objects.requireNonNull(settings, "settings is null");
        this.warmUp = new Settings(settings);
        return this;
    }

    public TestAppBuilder warmUp(Consumer<Settings> settings) {
        settings.accept(warmUp);
        return this;
    }


    public TestAppBuilder settings(Settings settings) {
        Objects.requireNonNull(settings, "settings is null");
        this.settings = new Settings(settings);
        return this;
    }

    public TestAppBuilder settings(Consumer<Settings> settings) {
        settings.accept(this.settings);
        return this;
    }


    public TestAppBuilder barrier(BarrierBuilder barrier) {
        this.barrier = barrier;
        return this;
    }


    public TestAppBuilder delayer(Delayer delayer) {
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
    public TestAppBuilder limiter(Limiter limiter) {
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


    public TestAppBuilder provide(Class<?> cls, Supplier<?> supplier) {
        provider.add(cls, supplier);
        return this;
    }

    public TestAppBuilder provide(Consumer<ProviderBuilder> builder) {
        builder.accept(provider);
        return this;
    }

    private <T> TestSuiteBuilder<T> newSuiteBuilder() {
        return new TestSuiteBuilder<T>()
                .delayer(delayer)
                .limiter(limiter)
                .settings(settings)
                .warmUp(warmUp);
    }

    public <T> TestAppBuilder addSuite(TestSuiteBuilder<T> builder) {
        suites.add(builder);
        return this;
    }

    public <T> TestAppBuilder createSuite(Consumer<TestSuiteBuilder<T>> consumer) {
        TestSuiteBuilder<T> b = newSuiteBuilder();
        consumer.accept(b);

        return addSuite(b);
    }

    public <T, A> TestAppBuilder createSuite(BiConsumer<TestSuiteBuilder<T>, A> consumer, A a) {
        TestSuiteBuilder<T> builder = newSuiteBuilder();
        consumer.accept(builder, a);

        return addSuite(builder);
    }

    public <T, A, B> TestAppBuilder createSuite(TriConsumer<TestSuiteBuilder<T>, A, B> consumer, A a, B b) {
        TestSuiteBuilder<T> builder = newSuiteBuilder();
        consumer.accept(builder, a, b);

        return addSuite(builder);
    }

    public <T> TestAppBuilder testSuiteObject(T testSuite) {
        return testSuite((Class<T>) testSuite.getClass(), () -> testSuite);
    }

    public <T> TestAppBuilder testSuiteClass(Class<T> cls) {
        return testSuiteClass(cls, b -> b.add(provider));
    }

    public <T> TestAppBuilder testSuiteClass(Class<T> cls, Consumer<ProviderBuilder> consumer) {
        ProviderBuilder b = new ProviderBuilder()
                .add(provider);
        consumer.accept(b);

        return testSuite(cls, () -> {
            try {
                log.warn("create test suite instance: {}", cls);
                ProviderBuilder.Context context = b.build();
                Constructor<?> selected = null;

                for (Constructor<?> constructor : cls.getConstructors()) {
                    if (!isCompatibleConstructor(constructor, context))
                        continue;

                    if (selected != null)
                        log.warn("found at least one matching constructor: {}, already found: {}, check it", constructor, selected);

                    selected = constructor;
                }

                if (selected == null)
                    throw new Exception("constructor not found");

                Object testSuite = selected
                        .newInstance(context.getArgs());

                return cls.cast(testSuite);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create new instance of test suite", e);
            }
        });
    }

    private boolean isCompatibleConstructor(Constructor<?> constructor, ProviderBuilder.Context context) {
        int modifiers = constructor.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            log.debug("private constructor: {}, skip", constructor);
            return false;
        }

        Class<?>[] params = constructor.getParameterTypes();
        log.debug("check constructor: {}, params: {}", constructor, params);
        for (Class<?> param : params) {
            Supplier<?> arg = context.getByType(param);
            if (arg == null) {
                log.debug("argument for param: {} not found, skip", param);
                return false;
            }
        }

        return true;
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
        TestSuiteBuilder<T> b = new TestSuiteProcessor<T>(newSuiteBuilder())
                .process(cls, supplier);

        return addSuite(b);
    }

    public BomberApp build() {
        Objects.requireNonNull(name,     "name is null");
        Preconditions.checkArgument(!suites.isEmpty(), "No test suites found");

        WorkerPool pool = new WorkerPool();
        Environment env = new Environment(sinks, watchers, () -> delayer, limiter, barrier);
        List<TestSuite<?>> suites = this.suites.stream()
                .map(b -> b.build(env))
                .collect(Collectors.toList());

        return new TestApp(name, pool, env, suites);
    }
}
