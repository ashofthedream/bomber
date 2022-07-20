package ashes.of.bomber.watcher;

import ashes.of.bomber.snapshots.TestFlightSnapshot;

public interface Watcher {
    void watch(TestFlightSnapshot snapshot);
}
