package ashes.of.bomber.core;

import java.util.List;

public class Plan {
    private final long id;
    private final List<SuitePlan> testSuites;

    public Plan(long id, List<SuitePlan> testSuites) {
        this.id = id;
        this.testSuites = testSuites;
    }

    public long getId() {
        return id;
    }

    public List<SuitePlan> getTestSuites() {
        return testSuites;
    }
}
