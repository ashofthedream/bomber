package ashes.of.bomber.atc.services;

import ashes.of.bomber.atc.models.Flight;
import ashes.of.bomber.carrier.dto.events.SinkEvent;
import ashes.of.bomber.flight.plan.TestFlightPlan;
import com.google.common.io.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class FlightService {
    private static final Logger log = LogManager.getLogger();

    private final AtomicLong flightIdSeq = new AtomicLong();
    private final Map<Long, Flight> flights = new ConcurrentHashMap<>();

    @Nullable
    private volatile Flight active;

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
        return flight;
    }

    public void handle(SinkEvent event) {
        var flight = flights.computeIfAbsent(event.getFlightId(), flightId -> new Flight(new TestFlightPlan(flightId, List.of())));
        flight.add(event);

        if (flight.isOver()) {
            log.info("Looks like flight: {} is over, remove from active", flight.getPlan().getFlightId());
//            active = null;
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
