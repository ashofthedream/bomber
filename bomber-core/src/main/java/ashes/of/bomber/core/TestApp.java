package ashes.of.bomber.core;

import ashes.of.bomber.flight.plan.TestAppPlan;
import ashes.of.bomber.flight.plan.TestCasePlan;
import ashes.of.bomber.flight.plan.TestSuitePlan;

import java.util.List;
import java.util.stream.Collectors;


public class TestApp {

    private final String name;
    private final List<TestSuite<?>> testSuites;


    public TestApp(String name, List<TestSuite<?>> testSuites) {
        this.name = name;
        this.testSuites = testSuites;
    }

    public String getName() {
        return name;
    }

    public List<TestSuite<?>> getTestSuites() {
        return testSuites;
    }

    public TestAppPlan createDefaultAppPlan() {
        var suites = getTestSuites().stream()
                .map(testSuite -> {
                    List<TestCasePlan> testCases = testSuite.getTestCases().stream()
                            .map(testCase -> new TestCasePlan(testCase.getName(), testCase.getConfiguration()))
                            .collect(Collectors.toList());

                    return new TestSuitePlan(testSuite.getName(), testCases);
                })
                .collect(Collectors.toList());

        return new TestAppPlan(name, suites);
    }
}
