package ashes.of.bomber.watcher;

import ashes.of.bomber.descriptions.TestAppDescription;

public interface Watcher {

    default void startUp() {}

    void watch(TestAppDescription app);

    default void shutDown() {}
}
