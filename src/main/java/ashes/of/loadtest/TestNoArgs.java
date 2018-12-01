package ashes.of.loadtest;

/**
 * Test method without any arguments
 */
@FunctionalInterface
public interface TestNoArgs<T extends TestCase> {
    void run(T testCase) throws Throwable;
}
