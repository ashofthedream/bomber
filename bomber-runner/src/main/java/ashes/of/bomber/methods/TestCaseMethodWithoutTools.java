package ashes.of.bomber.methods;

/**
 * Test method without any arguments
 */
@FunctionalInterface
public interface TestCaseMethodWithoutTools<T> {
    void run(T testCase) throws Throwable;
}
