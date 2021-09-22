package ashes.of.bomber.core;

import ashes.of.bomber.configuration.Configuration;
import ashes.of.bomber.methods.TestCaseMethod;
import ashes.of.bomber.tools.Tools;

public class TestCase<T> {
    private final String name;
    private final boolean async;
    private final Configuration configuration;

    private final TestCaseMethod<T> test;

    public TestCase(String name, boolean async, Configuration configuration, TestCaseMethod<T> test) {
        this.name = name;
        this.async = async;
        this.configuration = configuration;
        this.test = test;
    }

    public String getName() {
        return name;
    }

    public boolean isAsync() {
        return async;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void run(T instance, Tools tools) throws Throwable {
        this.test.run(instance, tools);
    }
}
