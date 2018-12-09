package ashes.of.loadtest;

/**
 * Test method without any arguments
 */
@FunctionalInterface
public interface TestNoArgs<T> {
    void run(T testCase) throws Throwable;
}
