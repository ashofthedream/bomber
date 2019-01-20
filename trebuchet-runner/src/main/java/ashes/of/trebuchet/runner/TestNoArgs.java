package ashes.of.trebuchet.runner;

/**
 * Test method without any arguments
 */
@FunctionalInterface
public interface TestNoArgs<T> {
    void run(T testCase) throws Throwable;
}
