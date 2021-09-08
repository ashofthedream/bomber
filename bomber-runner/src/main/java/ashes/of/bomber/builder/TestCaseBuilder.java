package ashes.of.bomber.builder;

import ashes.of.bomber.flight.SettingsBuilder;
import ashes.of.bomber.methods.TestCaseWithTools;
import ashes.of.bomber.runner.TestCase;

import java.util.Objects;
import java.util.function.Consumer;

public class TestCaseBuilder<T> {
    private String name;
    private boolean async;
    private ConfigurationBuilder config = new ConfigurationBuilder();
    private TestCaseWithTools<T> test;

    public TestCaseBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    public TestCaseBuilder<T> async(boolean async) {
        this.async = async;
        return this;
    }

    public TestCaseBuilder<T> async() {
        return async(true);
    }

    public TestCaseBuilder<T> config(Consumer<ConfigurationBuilder> consumer) {
        consumer.accept(this.config);
        return this;
    }

    public TestCaseBuilder<T> config(ConfigurationBuilder config) {
        this.config = config;
        return this;
    }

    public TestCaseBuilder<T> test(TestCaseWithTools<T> test) {
        this.test = test;
        return this;
    }

    public TestCase<T> build() {
        Objects.requireNonNull(name, "name is null");
        Objects.requireNonNull(test, "test is null");
        return new TestCase<>(name, async, config.build(), test);
    }
}
