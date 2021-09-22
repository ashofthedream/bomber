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
import java.util.concurrent.atomic.AtomicLong;

import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_CASE_HISTOGRAM;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_CASE_PROGRESS;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_FLIGHT_FINISH;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_FLIGHT_START;

public class Flight {
    private volatile int carriersCount;
    private final AtomicLong carriersStarted = new AtomicLong();
    private final AtomicLong carriersFinished = new AtomicLong();

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
        if (event.getType() == TEST_CASE_PROGRESS) {
            progress.put(event.getCarrierId(), event);
        }

        if (event.getType() == TEST_CASE_HISTOGRAM) {
            var byCarrier = histogram.computeIfAbsent(event.getCarrierId(), ts -> new ConcurrentSkipListMap<>());
            event.getHistograms()
                    .forEach(point -> byCarrier.put(point.getTimestamp(), point));

        }

        if (event.getType() == TEST_FLIGHT_START) {
            carriersStarted.incrementAndGet();
        }

        if (event.getType() == TEST_FLIGHT_FINISH) {
            carriersFinished.incrementAndGet();
        }

        events.add(event);
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

    public Flight setCarriersCount(int carriersCount) {
        this.carriersCount = carriersCount;
        return this;
    }

    public boolean isOver() {
        var started = carriersStarted.get();
        return started > 0 && started == carriersFinished.get();
    }
}
