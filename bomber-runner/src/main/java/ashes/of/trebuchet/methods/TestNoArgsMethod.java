package ashes.of.trebuchet.methods;

/**
 * Test method without any arguments
 */
@FunctionalInterface
public interface TestNoArgsMethod<T> {
    void run(T testCase) throws Throwable;
}
