package ashes.of.bomber.builder;

import ashes.of.bomber.core.TestApp;
import ashes.of.bomber.core.TestSuite;
import ashes.of.bomber.processors.TestAppProcessor;
import ashes.of.bomber.processors.TestSuiteProcessor;
import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.TriConsumer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TestAppBuilder {
    private static final Logger log = LogManager.getLogger();

    private String name;

    private ProviderBuilder provider = new ProviderBuilder();
    private ConfigurationBuilder config = new ConfigurationBuilder();

    /**
     * Test suites for run
     */
    private final List<TestSuiteBuilder<?>> testSuites = new ArrayList<>();

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

    public TestAppBuilder config(Consumer<ConfigurationBuilder> consumer) {
        consumer.accept(config);
        return this;
    }

    public TestAppBuilder config(ConfigurationBuilder config) {
        this.config = config;
        return this;
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
                .config(new ConfigurationBuilder(config));
    }

    public <T> TestAppBuilder createSuite(Consumer<TestSuiteBuilder<T>> consumer) {
        TriConsumer<TestSuiteBuilder<T>, Void, Void> c = (builder, a1, a2) -> consumer.accept(builder);
        return createSuite(c, null, null);
    }

    public <T, A> TestAppBuilder createSuite(BiConsumer<TestSuiteBuilder<T>, A> consumer, A arg1) {
        TriConsumer<TestSuiteBuilder<T>, A, Void> c = (builder, a1, b) -> consumer.accept(builder, a1);
        return createSuite(c, arg1, null);
    }

    public <T, A, B> TestAppBuilder createSuite(TriConsumer<TestSuiteBuilder<T>, A, B> consumer, A arg1, B arg2) {
        TestSuiteBuilder<T> builder = newSuiteBuilder();
        consumer.accept(builder, arg1, arg2);

        return addSuite(builder);
    }

    public <T> TestAppBuilder addSuite(TestSuiteBuilder<T> builder) {
        testSuites.add(builder);
        return this;
    }


    public <T> TestAppBuilder testSuiteObject(T testSuite) {
        return testSuite((Class<T>) testSuite.getClass(), () -> testSuite);
    }

    public <T> TestAppBuilder testSuiteClass(Class<T> cls) {
        return testSuiteClass(cls, b -> b.addAll(provider));
    }

    public <T> TestAppBuilder testSuiteClass(Class<T> cls, Consumer<ProviderBuilder> consumer) {
        ProviderBuilder b = new ProviderBuilder()
                .addAll(provider);

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

    public TestApp build() {
        Objects.requireNonNull(name, "name is null");
        Preconditions.checkArgument(!testSuites.isEmpty(), "No test suites found");

        List<TestSuite<?>> suites = this.testSuites.stream()
                .filter(TestSuiteBuilder::hasTestCases)
                .map(TestSuiteBuilder::build)
                .collect(Collectors.toList());

        return new TestApp(name, suites);
    }
}
