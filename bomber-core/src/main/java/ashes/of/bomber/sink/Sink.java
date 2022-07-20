package ashes.of.bomber.sink;

import ashes.of.bomber.tools.Record;
import ashes.of.bomber.events.EventHandler;
import ashes.of.bomber.events.FlightFinishedEvent;
import ashes.of.bomber.events.FlightStartedEvent;
import ashes.of.bomber.events.TestAppFinishedEvent;
import ashes.of.bomber.events.TestAppStartedEvent;
import ashes.of.bomber.events.TestCaseAfterEachEvent;
import ashes.of.bomber.events.TestCaseBeforeEachEvent;
import ashes.of.bomber.events.TestCaseFinishedEvent;
import ashes.of.bomber.events.TestCaseStartedEvent;
import ashes.of.bomber.events.TestSuiteFinishedEvent;
import ashes.of.bomber.events.TestSuiteStartedEvent;

public interface Sink {

    default void configure(EventHandler handler) {
        handler .handle(Record.class, this::timeRecorded)
                .handle(FlightStartedEvent.class, this::beforeFlight)
                .handle(TestAppStartedEvent.class, this::beforeTestApp)
                .handle(TestSuiteStartedEvent.class, this::beforeTestSuite)
                .handle(TestCaseStartedEvent.class, this::beforeTestCase)
                .handle(TestCaseBeforeEachEvent.class, this::beforeEach)
                .handle(TestCaseAfterEachEvent.class, this::afterEach)
                .handle(TestCaseFinishedEvent.class, this::afterTestCase)
                .handle(TestSuiteFinishedEvent.class, this::afterTestSuite)
                .handle(TestAppFinishedEvent.class, this::afterTestApp)
                .handle(FlightFinishedEvent.class, this::afterFlight)
        ;
    }

    /**
     * Invokes when time was recorded
     *
     * @param record call time record
     * Invokes then test app shuts down
     */
    default void timeRecorded(Record record) {
    }

    /**
     * Invokes then test flight starts
     */
    default void beforeFlight(FlightStartedEvent event) {}

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

    /**
     * Invokes then test app shuts down
     */
    default void afterFlight(FlightFinishedEvent event) {}

}
