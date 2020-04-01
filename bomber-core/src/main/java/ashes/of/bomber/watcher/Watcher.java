package ashes.of.bomber.watcher;

import ashes.of.bomber.core.BomberApp;

public interface Watcher {

    default void startUp() {}

    default void testSuiteStart(BomberApp app) {}

    default void testCaseStart(BomberApp app) {}

    void watch(BomberApp app);

    default void testCaseEnd(BomberApp app) {}

    default void testSuiteEnd(BomberApp app) {}

    default void shutDown() {}
}
