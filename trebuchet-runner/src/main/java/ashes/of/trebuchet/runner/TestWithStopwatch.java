package ashes.of.trebuchet.runner;

import ashes.of.trebuchet.stopwatch.Stopwatch;

/**
 * Test method with {@link Stopwatch}
 */
@FunctionalInterface
public interface TestWithStopwatch<T> {
    void run(T testCase, Stopwatch stopwatch) throws Throwable;
}
