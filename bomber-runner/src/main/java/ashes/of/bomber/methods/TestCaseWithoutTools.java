package ashes.of.bomber.methods;

/**
 * Test method without any arguments
 */
@FunctionalInterface
public interface TestCaseWithoutTools<T> {
    void run(T suite) throws Throwable;
}
