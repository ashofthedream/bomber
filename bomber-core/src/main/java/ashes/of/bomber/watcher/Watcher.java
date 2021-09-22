package ashes.of.bomber.watcher;

import ashes.of.bomber.events.EventHandler;
import ashes.of.bomber.snapshots.TestFlightSnapshot;

public interface Watcher extends EventHandler {
    void watch(TestFlightSnapshot flight);
}
