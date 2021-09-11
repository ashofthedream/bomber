package ashes.of.bomber.sink;

import ashes.of.bomber.events.TestAppFinishedEvent;
import ashes.of.bomber.events.TestAppStartedEvent;
import ashes.of.bomber.events.TestCaseAfterEachEvent;
import ashes.of.bomber.events.TestCaseBeforeEachEvent;
import ashes.of.bomber.events.TestCaseFinishedEvent;
import ashes.of.bomber.events.TestCaseStartedEvent;
import ashes.of.bomber.events.TestSuiteFinishedEvent;
import ashes.of.bomber.events.TestSuiteStartedEvent;
import ashes.of.bomber.flight.Iteration;
import ashes.of.bomber.tools.Record;

import javax.annotation.Nullable;


public interface Sink {

    /**
     * Invokes then app starts
     */
    default void beforeTestApp(TestAppStartedEvent event) {}

    /**
     * Invokes before all test cases in suite
     */
    default void beforeTestSuite(TestSuiteStartedEvent event) {}

    /**
     * Invokes before test case run
     */
    default void beforeTestCase(TestCaseStartedEvent event) {}

    /**
     * Invokes after each test case invocation
     */
    default void beforeEach(TestCaseBeforeEachEvent event) {}

    /**
     * Invokes when time was recorded
     *
     * @param record call time record
     */
    default void timeRecorded(Record record) {}

    /**
     * Invokes after each test case invocation
     */
    default void afterEach(TestCaseAfterEachEvent event) {}

    /**
     * Invokes before test case run
     */
    default void afterTestCase(TestCaseFinishedEvent event) {}

    /**
     * Invokes after all tests in test suite
     */
    default void afterTestSuite(TestSuiteFinishedEvent event) {}

    /**
     * Invokes then test app shuts down
     */
    default void afterTestApp(TestAppFinishedEvent event) {}
}
