package ashes.of.bomber.runner;

import ashes.of.bomber.methods.TestCaseMethodWithTools;

public class TestCase<T> {
    private final String name;
    private final boolean async;
    private final TestCaseMethodWithTools<T> method;

    public TestCase(String name, boolean async, TestCaseMethodWithTools<T> method) {
        this.name = name;
        this.async = async;
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public boolean isAsync() {
        return async;
    }

    public TestCaseMethodWithTools<T> getMethod() {
        return method;
    }
}
