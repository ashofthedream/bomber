package ashes.of.bomber.watcher;

import ashes.of.bomber.events.EventHandler;
import ashes.of.bomber.snapshots.FlightSnapshot;

public interface Watcher extends EventHandler {
    void watch(FlightSnapshot snapshot);
}
