package ashes.of.bomber.watcher;

import ashes.of.bomber.core.State;

public interface Watcher {

    default void startUp() {}

    default void testSuiteStart(State state) {}

    default void testCaseStart(State state) {}

    void watch(State state);

    default void testCaseEnd(State state) {}

    default void testSuiteEnd(State state) {}

    default void shutDown() {}
}
