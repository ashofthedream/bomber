package ashes.of.bomber.runner;

import ashes.of.bomber.core.Settings;
import ashes.of.bomber.methods.TestCaseMethodWithTools;
import ashes.of.bomber.tools.Tools;

import java.util.function.Supplier;

public class TestCase<T> {
    private final String name;
    private final boolean async;
    private final TestCaseMethodWithTools<T> method;
    private final Supplier<Settings> warmUp;
    private final Supplier<Settings> loadTest;

    public TestCase(String name, boolean async, TestCaseMethodWithTools<T> method, Supplier<Settings> warmUp, Supplier<Settings> loadTest) {
        this.name = name;
        this.async = async;
        this.method = method;
        this.warmUp = warmUp;
        this.loadTest = loadTest;
    }

    public String getName() {
        return name;
    }

    public boolean isAsync() {
        return async;
    }

    public Settings getWarmUp() {
        return warmUp.get();
    }

    public Settings getLoadTest() {
        return loadTest.get();
    }

    public void run(T instance, Tools tools) throws Throwable {
        this.method.run(instance, tools);
    }
}
