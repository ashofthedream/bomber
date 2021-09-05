package ashes.of.bomber.atc.controllers;

import ashes.of.bomber.atc.dto.flights.FlightDataDto;
import ashes.of.bomber.atc.dto.flights.FlightDto;
import ashes.of.bomber.atc.dto.flights.FlightRecordDto;
import ashes.of.bomber.atc.dto.flights.FlightsStartedDto;
import ashes.of.bomber.atc.model.Flight;
import ashes.of.bomber.atc.model.FlightRecord;
import ashes.of.bomber.atc.services.CarrierService;
import ashes.of.bomber.atc.services.FlightService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                .map(this::toFlight)
                .collectList();
    }

    @RequestMapping("/atc/flights/active")
    public Mono<FlightDto> getActive() {
        return flightService.getActive()
                .map(this::toFlight);
    }

    // start with flight plan
    @PostMapping("/atc/flights/start")
    public Mono<FlightsStartedDto> start() {
        log.debug("start all flights on all active carriers");

        Flight flight = flightService.startFlight();
        return carrierService.getCarriers()
                .flatMap(carrier -> carrierService.start(carrier, flight))
                .collectList()
                .map(flights -> new FlightsStartedDto()
                        .setFlights(flights));
    }

    @PostMapping("/atc/flights/{carrierId}/applications/{appId}/start")
    public Mono<FlightsStartedDto> startApplicationOnCarrierById(@PathVariable("carrierId") String carrierId, @PathVariable("appId") String appId) {
        log.debug("start application: {} on carrier: {}", appId, carrierId);

        Flight flight = flightService.startFlight();
        return carrierService.getCarrier(carrierId)
                .flatMap(carrier -> carrierService.start(carrier, flight))
                .map(started -> new FlightsStartedDto()
                        .setFlights(List.of(started)));
    }


    private FlightDto toFlight(Flight flight) {
        Map<String, FlightDataDto> all = new HashMap<>();

        flight.getProgress().forEach((carrierId, data) -> {

            List<FlightRecordDto> records = data.getRecords().stream()
                    .map(this::toFlightRecord)
                    .collect(Collectors.toList());

            FlightRecordDto actual = toFlightRecord(data.getActual());

            FlightDataDto dto = new FlightDataDto()
                    .setCarrierId(carrierId)
                    .setRecords(records)
                    .setActual(actual);

            all.put(carrierId, dto);
        });

        return new FlightDto()
                .setId(flight.getId())
                .setData(all);
    }

    @Nullable
    private FlightRecordDto toFlightRecord(@Nullable FlightRecord record) {
        if (record == null)
            return null;

        return new FlightRecordDto()
                .setTimestamp(record.getTimestamp())
                .setType(record.getType())
                .setTestSuite(record.getTestSuite())
                .setTestCase(record.getTestCase())
                .setState(record.getState());
    }
}
