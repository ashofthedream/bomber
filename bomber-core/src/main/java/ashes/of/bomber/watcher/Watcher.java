package ashes.of.bomber.watcher;

import ashes.of.bomber.core.BomberApp;

public interface Watcher {

    default void startUp() {}

    void watch(BomberApp app);

    default void shutDown() {}
}
