package ashes.of.bomber.runner;

import ashes.of.bomber.flight.Settings;
import ashes.of.bomber.methods.TestCaseWithTools;
import ashes.of.bomber.tools.Tools;

import java.util.function.Supplier;

public class TestCase<T> {
    private final String name;
    private final boolean async;
    private final TestCaseWithTools<T> method;
    private final Supplier<Settings> warmUp;
    private final Supplier<Settings> settings;

    public TestCase(String name, boolean async, TestCaseWithTools<T> method, Supplier<Settings> warmUp, Supplier<Settings> settings) {
        this.name = name;
        this.async = async;
        this.method = method;
        this.warmUp = warmUp;
        this.settings = settings;
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

    public Settings getSettings() {
        return settings.get();
    }

    public void run(T instance, Tools tools) throws Throwable {
        this.method.run(instance, tools);
    }
}
