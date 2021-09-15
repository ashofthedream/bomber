package ashes.of.bomber.events;

public interface EventHandler {

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
