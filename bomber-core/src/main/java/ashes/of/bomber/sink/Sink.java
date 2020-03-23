package ashes.of.bomber.sink;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;
import ashes.of.bomber.stopwatch.Record;

import javax.annotation.Nullable;
import java.time.Instant;


public interface Sink {

    /**
     * Invokes then app starts
     */
    default void afterStartUp() {}

    /**
     * Invokes before all test cases in suite
     *
     * @param stage     stage
     * @param testSuite test suite name
     * @param startTime stage start time
     * @param settings  stage settings
     */
    default void beforeTestSuite(Stage stage, String testSuite, Instant startTime, Settings settings) {}

    /**
     * Invokes before test case run
     *
     * @param stage     stage
     * @param testSuite test suite name
     * @param testCase  test suite name
     * @param startTime stage start time
     * @param settings  stage settings
     */
    default void beforeTestCase(Stage stage, String testSuite, String testCase, Instant startTime, Settings settings) {}

    /**
     * Invokes when time was recored
     *
     * @param context test context
     * @param record  lap record
     */
    default void timeRecorded(Context context, Record record) {}

    /**
     * Invokes after each test case invocation
     *
     * @param context   test context
     * @param elapsed   elapsed time in nanoseconds
     * @param throwable thrown exception, if methods throws an exception
     */
    default void afterEach(Context context, long elapsed, @Nullable Throwable throwable) {}

    /**
     * Invokes before test case run
     *
     * @param stage     stage
     * @param testSuite test suite name
     * @param testCase  test suite name
     * @param settings  stage settings
     */
    default void afterTestCase(Stage stage, String testSuite, String testCase) {}

    /**
     * Invokes after all tests in test suite
     *
     * @param stage     stage
     * @param testSuite test suite name
     * @param settings  stage settings
     */
    default void afterTestSuite(Stage stage, String testSuite) {}

    /**
     * Invokes then app
     */
    default void beforeShutDown() {}
}
