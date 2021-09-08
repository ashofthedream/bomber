package ashes.of.bomber.methods;

import ashes.of.bomber.tools.Tools;

/**
 * Test method with {@link Tools}
 */
@FunctionalInterface
public interface TestCaseMethod<T> {
    void run(T suite, Tools tools) throws Throwable;
}
