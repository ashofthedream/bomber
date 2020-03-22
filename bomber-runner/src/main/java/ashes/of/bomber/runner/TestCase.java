package ashes.of.bomber.runner;

import ashes.of.bomber.methods.TestCaseMethodWithClick;

public class TestCase<T> {
    private final String name;
    private final boolean async;
    private final TestCaseMethodWithClick<T> method;

    public TestCase(String name, boolean async, TestCaseMethodWithClick<T> method) {
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

    public TestCaseMethodWithClick<T> getMethod() {
        return method;
    }
}
