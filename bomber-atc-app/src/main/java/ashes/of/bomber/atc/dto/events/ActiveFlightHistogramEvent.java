package ashes.of.bomber.atc.dto.events;

import ashes.of.bomber.carrier.dto.events.HistogramPointDto;

import java.util.List;

public class ActiveFlightHistogramEvent {
    private final EventType type = EventType.ACTIVE_FLIGHT_HISTOGRAM;
    private List<HistogramPointDto> points;

    public EventType getType() {
        return type;
    }

    public List<HistogramPointDto> getPoints() {
        return points;
    }

    public ActiveFlightHistogramEvent setPoints(List<HistogramPointDto> points) {
        this.points = points;
        return this;
    }
}
