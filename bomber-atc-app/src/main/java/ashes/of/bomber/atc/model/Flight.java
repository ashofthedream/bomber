package ashes.of.bomber.atc.model;

import ashes.of.bomber.carrier.dto.events.HistogramPointDto;
import ashes.of.bomber.carrier.dto.events.SinkEvent;
import ashes.of.bomber.carrier.dto.flight.SettingsDto;
import ashes.of.bomber.carrier.dto.flight.TestAppSnapshotDto;
import ashes.of.bomber.carrier.dto.flight.TestFlightSnapshotDto;
import ashes.of.bomber.carrier.dto.flight.TestSuiteSnapshotDto;
import ashes.of.bomber.flight.plan.TestFlightPlan;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
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

    private final Map<String, FlightProgress> progress = new ConcurrentHashMap<>();
    private final Map<String, NavigableMap<Long, HistogramPointDto>> histogram = new ConcurrentHashMap<>();

    public Flight(TestFlightPlan plan) {
        this.plan = plan;
    }

    public TestFlightPlan getPlan() {
        return plan;
    }

    public void add(SinkEvent event) {
        if (event.getType() == TEST_CASE_PROGRESS) {
            Optional.ofNullable(event.getState())
                    .map(TestFlightSnapshotDto::getCurrent)
                    .map(TestAppSnapshotDto::getCurrent)
                    .map(TestSuiteSnapshotDto::getCurrent)
                    .ifPresent(snapshot -> {
                        var settings = snapshot.getSettings();

                        progress.put(event.getCarrierId(), new FlightProgress()
                                .setTestApp(event.getTestApp())
                                .setTestSuite(event.getTestSuite())
                                .setTestCase(event.getTestCase())
                                .setTimeElapsed(System.currentTimeMillis() - snapshot.getStartTime())
                                .setTimeTotal(settings.getDuration())
                                .setCurrentIterationsCount(snapshot.getCurrentIterationsCount())
                                .setTotalIterationsCount(settings.getTotalIterationsCount())
                                .setErrorsCount(snapshot.getErrorsCount())
                        );
                    });

            return;
        }

        if (event.getType() == TEST_CASE_HISTOGRAM) {
            var byCarrier = histogram.computeIfAbsent(event.getCarrierId(), ts -> new ConcurrentSkipListMap<>());
            event.getHistograms()
                    .forEach(point -> byCarrier.put(point.getTimestamp(), point));
            return;
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

    public Map<String, FlightProgress> getProgress() {
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
