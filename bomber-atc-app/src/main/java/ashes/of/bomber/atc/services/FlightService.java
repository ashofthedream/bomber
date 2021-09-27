package ashes.of.bomber.atc.services;

import ashes.of.bomber.atc.models.Flight;
import ashes.of.bomber.atc.models.FlightProgress;
import ashes.of.bomber.carrier.dto.events.SinkEvent;
import ashes.of.bomber.carrier.dto.flight.TestAppSnapshotDto;
import ashes.of.bomber.carrier.dto.flight.TestFlightSnapshotDto;
import ashes.of.bomber.carrier.dto.flight.TestSuiteSnapshotDto;
import ashes.of.bomber.flight.plan.TestFlightPlan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_CASE_PROGRESS;

@Service
public class FlightService {
    private static final Logger log = LogManager.getLogger();

    private final AtomicLong flightIdSeq = new AtomicLong();
    private final Map<Long, Flight> flights = new ConcurrentHashMap<>();

    @Nullable
    private volatile Flight active;

    private final WebSocketService webSocketService;

    public FlightService(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    public Mono<Flight> getActive() {
        return Mono.justOrEmpty(active);
    }

    // todo potential race condition
    public Flight startFlight(TestFlightPlan plan) {
        var current = this.active;
        if (current != null && !current.isOver()) {
            log.warn("Already has active flight and it's not over, but today I don't cate about it");
        }

        Flight flight = new Flight(plan);
        active = flight;
        flights.put(flight.getPlan().getFlightId(), flight);
        webSocketService.sendFlightStarted(flight);
        return flight;
    }

    public void handle(SinkEvent event) {
        var flight = flights.computeIfAbsent(event.getFlightId(), flightId -> new Flight(new TestFlightPlan(flightId, List.of())));
        flight.add(event);

        switch (event.getType()) {
            case TEST_CASE_PROGRESS:
                Optional.ofNullable(event.getSnapshot())
                        .map(TestFlightSnapshotDto::getCurrent)
                        .map(TestAppSnapshotDto::getCurrent)
                        .map(TestSuiteSnapshotDto::getCurrent)
                        .ifPresent(snapshot -> {
                            var settings = snapshot.getSettings();

                            var progress = new FlightProgress()
                                    .setTestApp(event.getTestApp())
                                    .setTestSuite(event.getTestSuite())
                                    .setTestCase(event.getTestCase())
                                    .setTimeElapsed(System.currentTimeMillis() - snapshot.getStartTime())
                                    .setTimeTotal(settings.getDuration())
                                    .setCurrentIterationsCount(snapshot.getCurrentIterationsCount())
                                    .setTotalIterationsCount(settings.getTotalIterationsCount())
                                    .setErrorsCount(snapshot.getErrorsCount());

                            flight.addProgress(event.getCarrierId(), progress);
                            webSocketService.sendFlightProgress(progress);
                        });
                break;

            case TEST_CASE_HISTOGRAM:
                flight.addHistogramPoints(event.getCarrierId(), event.getHistograms());
                webSocketService.sendFlightHistogram(event.getHistograms());
              break;

            default:
                webSocketService.sendFlightEvent(event);
        }



        if (flight.isOver()) {
            log.info("Looks like flight: {} is over, remove from active", flight.getPlan().getFlightId());
            webSocketService.sendFlightFinished(flight);
            active = null;
        }
    }

    public Flux<Flight> getFlights() {
        return Flux.fromIterable(flights.values());
    }

    public long getNextFlightId() {
        return flightIdSeq.incrementAndGet();
    }

    public Mono<Flight> getFlight(long flightId) {
        return Mono.justOrEmpty(flights.get(flightId));
    }
}
