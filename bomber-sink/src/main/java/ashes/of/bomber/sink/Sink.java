package ashes.of.bomber.sink;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;
import ashes.of.bomber.core.stopwatch.Record;

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
     * @param record  lap record
     */
    default void onTimeRecorded(Context context, Record record) {}

    /**
     * Invokes after test invocation
     *
     * @param context   test context
     * @param elapsed   elapsed time in nanoseconds
     * @param throwable thrown exception, if methods throws an exception
     */
    default void afterEachTest(Context context, long elapsed, @Nullable Throwable throwable) {}

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
