package ashes.of.bomber.methods;

/**
 * Test method without any arguments
 */
@FunctionalInterface
public interface TestCaseNoArgsMethod<T> {
    void run(T testCase) throws Throwable;
}
