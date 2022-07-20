package ashes.of.bomber.sink;

import ashes.of.bomber.events.EventHandler;
import ashes.of.bomber.tools.Record;

public interface Sink extends EventHandler {

    /**
     * Invokes when time was recorded
     *
     * @param record call time record
     */
    default void timeRecorded(Record record) {
    }
}
