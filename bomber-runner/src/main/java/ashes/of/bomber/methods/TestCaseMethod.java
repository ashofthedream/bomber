package ashes.of.bomber.methods;

import ashes.of.bomber.core.stopwatch.Clock;

/**
 * Test method with {@link Clock}
 */
@FunctionalInterface
public interface TestCaseMethod<T> {
    void run(T suite, Clock clock) throws Throwable;
}
