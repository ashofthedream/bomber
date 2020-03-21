package ashes.of.bomber.watcher;

import ashes.of.bomber.core.State;

public interface Watcher {

    default void onStart(State state) {}

    void watch(State state);

    default void onEnd(State state) {}
}
