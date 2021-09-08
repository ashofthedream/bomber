package ashes.of.bomber.runner;

import ashes.of.bomber.methods.TestCaseWithTools;
import ashes.of.bomber.tools.Tools;

public class TestCase<T> {
    private final String name;
    private final boolean async;
    private final Configuration configuration;

    private final TestCaseWithTools<T> test;

    public TestCase(String name, boolean async, Configuration configuration, TestCaseWithTools<T> test) {
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
