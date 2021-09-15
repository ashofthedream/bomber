package ashes.of.bomber.watcher;

import ashes.of.bomber.core.TestApp;

public interface Watcher {

    default void startUp() {}

    void watch(TestApp app);

    default void shutDown() {}
}
