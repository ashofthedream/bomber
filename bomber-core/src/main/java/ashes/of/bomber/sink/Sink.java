package ashes.of.bomber.sink;

import ashes.of.bomber.flight.Iteration;
import ashes.of.bomber.configuration.Settings;
import ashes.of.bomber.flight.Stage;
import ashes.of.bomber.tools.Record;

import javax.annotation.Nullable;
import java.time.Instant;


public interface Sink {

    /**
     * Invokes then app starts
     *
     * @param timestamp application start time
     */
    default void startUp(Instant timestamp) {}

    /**
     * Invokes before all test cases in suite
     *
     * @param timestamp test suite start time
     * @param testSuite test suite name
     */
    default void beforeTestSuite(Instant timestamp, String testSuite) {}

    /**
     * Invokes before test case run
     *
     * @param timestamp test case start time
     * @param stage     stage
     * @param testSuite test suite name
     * @param testCase  test suite name
     * @param settings  stage settings
     */
    default void beforeTestCase(Instant timestamp, Stage stage, String testSuite, String testCase, Settings settings) {}

    default void beforeEach(Iteration it) {}

    /**
     * Invokes when time was recorded
     *
     * @param record call time record
     */
    default void timeRecorded(Record record) {}

    /**
     * Invokes after each test case invocation
     *
     * @param it current iteration
     * @param elapsed   elapsed time in nanoseconds
     * @param throwable thrown exception, if methods throws an exception
     */
    default void afterEach(Iteration it, long elapsed, @Nullable Throwable throwable) {}

    /**
     * Invokes before test case run
     *
     * @param stage     stage
     * @param testSuite test suite name
     * @param testCase  test suite name
     */
    default void afterTestCase(Stage stage, String testSuite, String testCase) {}

    /**
     * Invokes after all tests in test suite
     *
     * @param testSuite test suite name
     */
    default void afterTestSuite(String testSuite) {}

    /**
     * Invokes then app
     *
     * @param timestamp application shutdown time
     */
    default void shutDown(Instant timestamp) {}
}
