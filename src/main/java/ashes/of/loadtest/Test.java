package ashes.of.loadtest;

import ashes.of.loadtest.stopwatch.Stopwatch;

/**
 * Test method
 */
@FunctionalInterface
public interface Test<T extends TestCase> {
    void run(T testCase, Stopwatch stopwatch) throws Throwable;
}
