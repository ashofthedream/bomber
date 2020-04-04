package ashes.of.bomber.core;

import java.util.List;

public class SuitePlan {
    private final String name;
    private final List<String> testCases;

    public SuitePlan(String name, List<String> testCases) {
        this.name = name;
        this.testCases = testCases;
    }

    public String getName() {
        return name;
    }

    public List<String> getTestCases() {
        return testCases;
    }
}
