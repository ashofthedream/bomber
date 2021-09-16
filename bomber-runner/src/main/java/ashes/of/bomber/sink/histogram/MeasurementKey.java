package ashes.of.bomber.sink.histogram;

import java.util.Objects;

public class MeasurementKey {
    private final String testApp;
    private final String testSuite;
    private final String testCase;

    public MeasurementKey(String testApp, String testSuite, String testCase) {
        this.testApp = testApp;
        this.testSuite = testSuite;
        this.testCase = testCase;
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
        MeasurementKey that = (MeasurementKey) o;
        return testApp.equals(that.testApp) &&
                testSuite.equals(that.testSuite) &&
                testCase.equals(that.testCase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testApp, testSuite, testCase);
    }
}
