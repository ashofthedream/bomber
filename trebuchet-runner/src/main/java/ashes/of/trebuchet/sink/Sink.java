package ashes.of.trebuchet.sink;

import ashes.of.trebuchet.runner.Context;
import ashes.of.trebuchet.builder.Settings;
import ashes.of.trebuchet.runner.Stage;
import ashes.of.trebuchet.stopwatch.Stopwatch;

import javax.annotation.Nullable;
import java.time.Instant;


public interface Sink {

    /**
     * Invokes before all test then stage starts
     *
     * @param stage     stage
     * @param testCase  test case name
     * @param startTime stage start time
     * @param settings  stage settings
     */
    default void beforeAll(Stage stage, String testCase, Instant startTime, Settings settings) {}

    /**
     * Invokes after test invocation
     *
     * @param context test context
     * @param elapsed elapsed time in nanoseconds
     * @param stopwatch stopwatch used in test
     * @param throwable thrown exception, if methods throws an exception
     */
    default void afterEach(Context context, long elapsed, Stopwatch stopwatch, @Nullable Throwable throwable) {}

    /**
     * Invokes after all tests then stage ends
     *
     * @param stage     stage
     * @param testCase  test case name
     * @param startTime stage start time
     * @param settings  stage settings
     */
    default void afterAll(Stage stage, String testCase, Instant startTime, Settings settings) {}
}
