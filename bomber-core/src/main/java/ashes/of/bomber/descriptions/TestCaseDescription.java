package ashes.of.bomber.descriptions;

public class TestCaseDescription {
    private final String name;
    private final boolean async;

    public TestCaseDescription(String name, boolean async) {
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
