package ashes.of.bomber.atc.controllers;

import ashes.of.bomber.atc.dto.flights.FlightDto;
import ashes.of.bomber.carrier.dto.requests.FlightStartedResponse;
import ashes.of.bomber.carrier.mappers.TestFlightMapper;
import ashes.of.bomber.atc.models.Flight;
import ashes.of.bomber.atc.services.CarrierService;
import ashes.of.bomber.atc.services.FlightService;
import ashes.of.bomber.carrier.dto.requests.StartFlightRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class FlightController {
    private static final Logger log = LogManager.getLogger();

    private final CarrierService carrierService;
    private final FlightService flightService;

    public FlightController(CarrierService carrierService, FlightService flightService) {
        this.carrierService = carrierService;
        this.flightService = flightService;
    }

    @RequestMapping("/atc/flights")
    public Mono<List<FlightDto>> getAll() {
        return flightService.getFlights()
                .map(this::toDto)
                .collectList();
    }

    @RequestMapping("/atc/flights/active")
    public Mono<FlightDto> getActive() {
        return flightService.getActive()
                .map(this::toDto);
    }

    @RequestMapping("/atc/flights/{flightId}")
    public Mono<FlightDto> getFlight(@PathVariable("flightId") long flightId) {
        return flightService.getFlight(flightId)
                .map(this::toDto);
    }

    // start with flight plan
    @PostMapping("/atc/flights/start")
    public Mono<FlightStartedResponse> start(@RequestBody StartFlightRequest request) {
        log.debug("start all flights on all active carriers");
        var plan = request.getFlight()
                .setId(flightService.getNextFlightId());

        var flight = flightService.startFlight(TestFlightMapper.toPlan(plan));
        return carrierService.getCarriers()
                .filter(carrier -> request.getCarriers().isEmpty() || request.getCarriers().contains(carrier.getId()))
                .flatMap(carrier -> carrierService.start(carrier, flight))
                .collectList()
                .map(flights -> {
                    flight.setCarriersCount(flights.size());
                    var flightIds = flights.stream()
                            .map(FlightStartedResponse::id)
                            .distinct()
                            .toList();

                    if (flightIds.size() > 1) {
                        log.warn("Non unique flights started: {}", flightIds);
                    }

                    var flightId = flightIds.stream()
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("No flights started"));

                    return new FlightStartedResponse(flightId);
                });
    }

    private FlightDto toDto(Flight flight) {
        return new FlightDto()
                .setPlan(TestFlightMapper.toDto(flight.getPlan()))
                .setEvents(flight.getEvents())
                .setProgress(flight.getProgress())
                .setHistogram(flight.getHistogram());
    }
}
