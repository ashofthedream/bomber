package ashes.of.bomber.sink.histogram;

import ashes.of.bomber.flight.Stage;

import java.util.Objects;

public class MeasurementKey {
    private final String testSuite;
    private final String testCase;
    private final Stage stage;

    public MeasurementKey(String testSuite, String testCase, Stage stage) {
        this.testSuite = testSuite;
        this.testCase = testCase;
        this.stage = stage;
    }

    public String getTestSuite() {
        return testSuite;
    }

    public String getTestCase() {
        return testCase;
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeasurementKey that = (MeasurementKey) o;
        return testSuite.equals(that.testSuite) && testCase.equals(that.testCase) && stage == that.stage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(testSuite, testCase, stage);
    }
}
