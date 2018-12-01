package ashes.of.loadtest;

import ashes.of.loadtest.stopwatch.Stopwatch;

/**
 * Test method with {@link Stopwatch}
 */
@FunctionalInterface
public interface TestWithStopwatch<T extends TestCase> {
    void run(T testCase, Stopwatch stopwatch) throws Throwable;
}
