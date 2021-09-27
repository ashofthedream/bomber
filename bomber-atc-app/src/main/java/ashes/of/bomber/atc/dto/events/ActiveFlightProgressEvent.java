package ashes.of.bomber.atc.dto.events;

import ashes.of.bomber.atc.models.FlightProgress;

public class ActiveFlightProgressEvent {
    private final EventType type = EventType.ACTIVE_FLIGHT_PROGRESS;
    private FlightProgress progress;

    public EventType getType() {
        return type;
    }

    public FlightProgress getProgress() {
        return progress;
    }

    public ActiveFlightProgressEvent setProgress(FlightProgress progress) {
        this.progress = progress;
        return this;
    }
}
