package ashes.of.bomber.methods;

import ashes.of.bomber.core.stopwatch.Stopwatch;

/**
 * Test method with {@link Stopwatch}
 */
@FunctionalInterface
public interface TestWithStopwatchMethod<T> {
    void run(T testCase, Stopwatch stopwatch) throws Throwable;
}
