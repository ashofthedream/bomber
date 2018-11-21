package ashes.of.loadtest;


/**
 * This test case defines the fixture to run multiple tests
 */
public interface TestCase {

    /**
     * This method will be invoked once in each thread before all test methods
     */
    default void beforeAll() throws Exception {}

    /**
     * This method will be invoked every time before each test method
     */
    default void beforeTest() throws Exception {}

    /**
     * This method will be invoked every time after each test method
     */
    default void afterTest() throws Exception {}

    /**
     * This method will be invoked once in each thread after all test methods
     */
    default void afterAll() throws Exception {}
}
