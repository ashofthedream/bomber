package ashes.of.bomber.methods;

import ashes.of.bomber.stopwatch.Tools;

/**
 * Test method with {@link Tools}
 */
@FunctionalInterface
public interface TestCaseMethodWithTools<T> {
    void run(T suite, Tools tools) throws Throwable;
}
