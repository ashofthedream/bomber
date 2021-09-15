package ashes.of.bomber.watcher;

import ashes.of.bomber.snapshots.FlightSnapshot;

public interface Watcher {

    default void startUp() {}

    void watch(FlightSnapshot snapshot);

    default void shutDown() {}
}
