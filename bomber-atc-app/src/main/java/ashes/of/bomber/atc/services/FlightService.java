package ashes.of.bomber.atc.services;

import ashes.of.bomber.atc.model.Flight;
import ashes.of.bomber.atc.model.FlightProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class FlightService {
    private static final Logger log = LogManager.getLogger();

    private final AtomicLong idSeq = new AtomicLong();

    private final Map<Long, Flight> flights = new ConcurrentHashMap<>();

    private volatile Flight active;
    private final CarrierService carrierService;

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
                    FlightProgress data = flight.getData(carrier.getId());
                    data.add(carrier.getApp().getState());
                });
    }


    public Mono<Flight> getActive() {
        return Mono.justOrEmpty(active);
    }

    // todo potential race condition
    public Flight startFlight() {
        if (active != null)
            throw new RuntimeException("Already started");

        Flight flight = new Flight(idSeq.incrementAndGet());
        active = flight;
        flights.put(flight.getId(), flight);
        return flight;
    }

    public Flux<Flight> getFlights() {
        return Flux.fromIterable(flights.values());
    }
}
