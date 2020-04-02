package ashes.of.bomber.atc.controllers;

import ashes.of.bomber.atc.dto.flights.FlightDto;
import ashes.of.bomber.atc.dto.flights.FlightDataDto;
import ashes.of.bomber.atc.dto.flights.FlightRecordDto;
import ashes.of.bomber.atc.dto.flights.FlightsStartedDto;
import ashes.of.bomber.atc.model.Flight;
import ashes.of.bomber.atc.services.CarrierService;
import ashes.of.bomber.atc.services.FlightService;
import ashes.of.bomber.carrier.dto.FlightStartedDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/atc/flights")
public class FlightController {
    private static final Logger log = LogManager.getLogger();

    private final CarrierService carrierService;
    private final FlightService flightService;

    public FlightController(CarrierService carrierService, FlightService flightService) {
        this.carrierService = carrierService;
        this.flightService = flightService;
    }

    @RequestMapping
    public Mono<List<FlightDto>> getAll() {
        return flightService.getFlights()
                .map(this::toFlight)
                .collectList();
    }

    @RequestMapping("/active")
    public Mono<FlightDto> getActive() {
        return flightService.getActive()
                .map(this::toFlight);
    }

    @PostMapping("/start")
    public Mono<FlightsStartedDto> start() {
        log.debug("start all flights on all active carriers");

        Flight flight = flightService.startFlight();
        return carrierService.getCarriers()
                .flatMap(carrier -> carrierService.start(carrier, flight))
                .collectList()
                .map(flights -> FlightsStartedDto.builder()
                        .flights(flights)
                        .build());
    }

    @PostMapping("/{carrierId}/applications/{appId}/start")
    public Mono<FlightsStartedDto> startApplicationOnCarrierById(@PathVariable("carrierId") String carrierId, @PathVariable("appId") String appId) {
        log.debug("start application: {} on carrier: {}", appId, carrierId);

        Flight flight = flightService.startFlight();
        return carrierService.getCarrier(carrierId)
                .flatMap(carrier -> carrierService.start(carrier, flight))
                .map(started -> FlightsStartedDto.builder()
                        .flights(Collections.singletonList(started))
                        .build());
    }


    private FlightDto toFlight(Flight flight) {
        Map<String, FlightDataDto> all = new HashMap<>();

        flight.getData().forEach((carrierId, data) -> {

            List<FlightRecordDto> records = data.getRecords().stream()
                    .map(this::toFlightRecord)
                    .collect(Collectors.toList());

            FlightRecordDto actual = toFlightRecord(data.getActual());

            FlightDataDto dto = FlightDataDto.builder()
                    .carrierId(carrierId)
                    .records(records)
                    .actual(actual)
                    .build();

            all.put(carrierId, dto);
        });

        return FlightDto.builder()
                .id(flight.getId())
                .data(all)
                .build();
    }

    @Nullable
    private FlightRecordDto toFlightRecord(@Nullable Flight.FlightRecord record) {
        if (record == null)
            return null;

        return FlightRecordDto.builder()
                .timestamp(record.getTimestamp())
                .type(record.getType())
                .testSuite(record.getTestSuite())
                .testCase(record.getTestCase())
                .state(record.getState())
                .build();
    }
}
