package ashes.of.bomber.core;

import ashes.of.bomber.flight.plan.TestAppPlan;
import ashes.of.bomber.flight.plan.TestCasePlan;
import ashes.of.bomber.flight.plan.TestSuitePlan;

import java.util.List;
import java.util.Map;
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

    public Map<String, TestSuite<?>> getTestSuitesByName() {
        return testSuites.stream()
                .collect(Collectors.toMap(TestSuite::getName, suite -> suite));
    }


}
