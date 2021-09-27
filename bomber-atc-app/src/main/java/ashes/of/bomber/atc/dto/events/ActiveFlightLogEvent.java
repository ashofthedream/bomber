package ashes.of.bomber.atc.dto.events;

import ashes.of.bomber.carrier.dto.events.SinkEvent;

public class ActiveFlightLogEvent {
    private final EventType type = EventType.ACTIVE_FLIGHT_LOG;
    private SinkEvent event;

    public EventType getType() {
        return type;
    }

    public SinkEvent getEvent() {
        return event;
    }

    public ActiveFlightLogEvent setEvent(SinkEvent event) {
        this.event = event;
        return this;
    }
}
