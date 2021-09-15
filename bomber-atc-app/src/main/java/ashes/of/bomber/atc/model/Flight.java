package ashes.of.bomber.atc.model;

import ashes.of.bomber.carrier.dto.events.HistogramPointDto;
import ashes.of.bomber.carrier.dto.events.SinkEvent;
import ashes.of.bomber.flight.plan.TestFlightPlan;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_CASE_HISTOGRAM;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_CASE_PROGRESS;

public class Flight {
    private final TestFlightPlan plan;
    private final Queue<SinkEvent> events = new PriorityQueue<>(Comparator.comparingLong(SinkEvent::getTimestamp));

    private final Map<String, SinkEvent> progress = new ConcurrentHashMap<>();
    private final Map<String, NavigableMap<Long, HistogramPointDto>> histogram = new ConcurrentHashMap<>();

    public Flight(TestFlightPlan plan) {
        this.plan = plan;
    }

    public TestFlightPlan getPlan() {
        return plan;
    }

    public void add(SinkEvent event) {
        events.add(event);

        if (event.getType() == TEST_CASE_PROGRESS) {
            progress.put(event.getCarrierId(), event);
        }

        if (event.getType() == TEST_CASE_HISTOGRAM) {
            var byCarrier = histogram.computeIfAbsent(event.getCarrierId(), ts -> new ConcurrentSkipListMap<>());
            event.getHistograms()
                    .forEach(point -> byCarrier.put(point.getTimestamp(), point));
        }
    }

    public List<SinkEvent> getEvents() {
        return List.copyOf(events);
    }

    public Map<String, SinkEvent> getProgress() {
        return progress;
    }

    public Map<String, NavigableMap<Long, HistogramPointDto>> getHistogram() {
        return histogram;
    }
}
