package ashes.of.bomber.atc.dto.events;

import ashes.of.bomber.carrier.dto.flight.TestFlightDto;

public class ActiveFlightEvent {
    private EventType type;
    private TestFlightDto plan;

    public EventType getType() {
        return type;
    }

    public ActiveFlightEvent setType(EventType type) {
        this.type = type;
        return this;
    }

    public TestFlightDto getPlan() {
        return plan;
    }

    public ActiveFlightEvent setPlan(TestFlightDto plan) {
        this.plan = plan;
        return this;
    }
}
