package ashes.of.bomber.methods;

/**
 * Test method without any arguments
 */
@FunctionalInterface
public interface TestNoArgsMethod<T> {
    void run(T testCase) throws Throwable;
}
