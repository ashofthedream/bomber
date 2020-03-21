package ashes.of.bomber.methods;

import ashes.of.bomber.core.stopwatch.Clock;

/**
 * Test method with {@link Clock}
 */
@FunctionalInterface
public interface TestWithClockMethod<T> {
    void run(T testCase, Clock stopwatch) throws Throwable;
}
