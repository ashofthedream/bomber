package ashes.of.bomber.core;

import java.util.Objects;

public class Test {
    private final String testApp;
    private final String testSuite;
    private final String testCase;

    public Test(String testApp, String testSuite, String testCase) {
        this.testApp = testApp;
        this.testSuite = testSuite;
        this.testCase = testCase;
    }

    public String getName() {
        return testApp + "." + testSuite + "." + testCase;
    }

    public String getTestApp() {
        return testApp;
    }

    public String getTestSuite() {
        return testSuite;
    }

    public String getTestCase() {
        return testCase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Test test = (Test) o;
        return testApp.equals(test.testApp) && testSuite.equals(test.testSuite) && testCase.equals(test.testCase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testApp, testSuite, testCase);
    }

    @Override
    public String toString() {
        return "Test{" +
                "testApp='" + testApp + '\'' +
                ", testSuite='" + testSuite + '\'' +
                ", testCase='" + testCase + '\'' +
                '}';
    }
}
