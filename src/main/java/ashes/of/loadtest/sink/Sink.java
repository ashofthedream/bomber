package ashes.of.loadtest.sink;

import ashes.of.loadtest.runner.TestCaseContext;
import ashes.of.loadtest.runner.TestContext;
import ashes.of.loadtest.settings.Settings;
import ashes.of.loadtest.Stage;
import ashes.of.loadtest.stopwatch.Stopwatch;

import javax.annotation.Nullable;
import java.time.Instant;


public interface Sink {

    /**
     * Invokes before stage starts
     *
     * @param stage     stage
     * @param testCase  test case name
     * @param startTime stage start time
     * @param settings  stage settings
     */
    default void beforeRun(Stage stage, String testCase, Instant startTime, Settings settings) {}

    /**
     * Invokes after test invocation
     *
     * @param context test context
     * @param elapsed elapsed time in nanoseconds
     * @param stopwatch stopwatch used in test
     * @param throwable thrown exception, if methods throws an exception
     */
    default void afterTest(TestContext context, long elapsed, Stopwatch stopwatch, @Nullable Throwable throwable) {}

    /**
     * Invokes after each invocation of all test ends in test case
     *
     * @param context test case context
     * @param elapsed elapsed time of all test in test cases in nanoseconds
     */
    default void afterAllTests(TestCaseContext context, long elapsed) {}

    /**
     * Invokes after stage ends
     *
     * @param stage     stage
     * @param testCase  test case name
     * @param startTime stage start time
     * @param settings  stage settings
     */
    default void afterRun(Stage stage, String testCase, Instant startTime, Settings settings) {}

}
