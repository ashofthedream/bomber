package ashes.of.bomber.core;

public class TestCaseModel {
    private final String name;
    private final boolean async;

    public TestCaseModel(String name, boolean async) {
        this.name = name;
        this.async = async;
    }

    public String getName() {
        return name;
    }

    public boolean isAsync() {
        return async;
    }
}
