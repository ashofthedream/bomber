package ashes.of.bomber.atc.dto.flights;

import ashes.of.bomber.carrier.dto.events.HistogramPointDto;
import ashes.of.bomber.carrier.dto.events.SinkEvent;
import ashes.of.bomber.carrier.dto.flight.TestFlightDto;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

public class FlightDto {
    private TestFlightDto plan;
    private List<SinkEvent> events;
    private Map<String, SinkEvent> progress;
    private Map<String, NavigableMap<Long, HistogramPointDto>> histogram;

    public TestFlightDto getPlan() {
        return plan;
    }

    public FlightDto setPlan(TestFlightDto plan) {
        this.plan = plan;
        return this;
    }

    public List<SinkEvent> getEvents() {
        return events;
    }

    public FlightDto setEvents(List<SinkEvent> events) {
        this.events = events;
        return this;
    }

    public Map<String, SinkEvent> getProgress() {
        return progress;
    }

    public FlightDto setProgress(Map<String, SinkEvent> progress) {
        this.progress = progress;
        return this;
    }

    public Map<String, NavigableMap<Long, HistogramPointDto>> getHistogram() {
        return histogram;
    }

    public FlightDto setHistogram(Map<String, NavigableMap<Long, HistogramPointDto>> histogram) {
        this.histogram = histogram;
        return this;
    }
}
