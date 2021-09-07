package ashes.of.bomber.atc.services;

import ashes.of.bomber.atc.model.Flight;
import ashes.of.bomber.atc.model.FlightProgress;
import ashes.of.bomber.atc.model.FlightRecord;
import ashes.of.bomber.carrier.dto.events.SinkEvent;
import ashes.of.bomber.flight.FlightPlan;
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
    private final CarrierService carrierService;

    @Nullable
    private volatile Flight active;

    public FlightService(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

//    @Scheduled(fixedRate = 1000)
    public void updateActiveFlight() {
        Flight flight = active;
        if (flight == null)
            return;

        carrierService.getCarriers()
                .flatMap(carrierService::status)
                .subscribe(carrier -> {
                    FlightProgress progress = flight.getOrCreateCarrierProgress(carrier.getId());
                    progress.add(carrier.getApp().getState());
                });
    }


    public Mono<Flight> getActive() {
        return Mono.justOrEmpty(active);
    }

    public void process(SinkEvent event) {
        Flight flight = active;
        if (flight == null) {
            log.warn("ATC hasn't active flight, but received event from carrier: {} with flight: {}. " +
                    "Flight will be created and marked as active. It's temporal solution",
                    event.getCarrierId(), event.getFlightId());

            var foundOrCreated = flights.computeIfAbsent(event.getFlightId(), planId -> new Flight(new FlightPlan(planId, List.of())));
            active = flight = foundOrCreated;
        }

        if (flight.getPlan().getFlightId() != event.getFlightId()) {
            log.warn("ATC received event from carrier: {} with flight: {}, bur current active flight: {}. Just ignore it",
                    event.getCarrierId(), event.getFlightId(), flight.getPlan().getFlightId());
            return;
        }

        FlightProgress progress = flight.getOrCreateCarrierProgress(event.getCarrierId());

        FlightRecord record = new FlightRecord(event.getType().name(), event.getTimestamp(), null);
        record.setTestSuite(event.getTestSuite());
        record.setTestCase(event.getTestCase());
        record.setState(event.getState());
        record.setHistograms(event.getHistograms());
        progress.add(record);
    }

    // todo potential race condition
    public Flight startFlight(FlightPlan plan) {
        if (active != null)
            throw new RuntimeException("Already started");

        Flight flight = new Flight(plan);
        active = flight;
        flights.put(flight.getPlan().getFlightId(), flight);
        return flight;
    }

    public Flux<Flight> getFlights() {
        return Flux.fromIterable(flights.values());
    }

    public long getNextFlightId() {
        return flightIdSeq.incrementAndGet();
    }
}
