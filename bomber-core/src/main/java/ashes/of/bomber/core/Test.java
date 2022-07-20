package ashes.of.bomber.core;

import java.util.Objects;

public record Test(String testApp, String testSuite, String testCase) {

    public String name() {
        return testApp + "." + testSuite + "." + testCase;
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
}
