package ashes.of.bomber.atc.services;

import ashes.of.bomber.atc.model.Flight;
import ashes.of.bomber.carrier.dto.events.SinkEvent;
import ashes.of.bomber.flight.plan.TestFlightPlan;
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
        if (active != null) {
            log.warn("Already stared, but today I don't cate about it");
//            throw new RuntimeException("Already started");
        }

        Flight flight = new Flight(plan);
        active = flight;
        flights.put(flight.getPlan().getFlightId(), flight);
        return flight;
    }

    public void process(SinkEvent event) {
        var flight = flights.computeIfAbsent(event.getFlightId(), flightId -> new Flight(new TestFlightPlan(flightId, List.of())));
        flight.add(event);
    }

    public Flux<Flight> getFlights() {
        return Flux.fromIterable(flights.values());
    }

    public long getNextFlightId() {
        return flightIdSeq.incrementAndGet();
    }
}
