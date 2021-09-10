package ashes.of.bomber.atc.controllers;

import ashes.of.bomber.atc.dto.flights.FlightProgressDto;
import ashes.of.bomber.atc.dto.flights.FlightDto;
import ashes.of.bomber.atc.dto.flights.FlightRecordDto;
import ashes.of.bomber.carrier.dto.requests.FlightsStartedResponse;
import ashes.of.bomber.carrier.mappers.TestFlightMapper;
import ashes.of.bomber.atc.model.Flight;
import ashes.of.bomber.atc.model.FlightRecord;
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
    public Mono<FlightsStartedResponse> start(@RequestBody StartFlightRequest request) {
        log.debug("start all flights on all active carriers");
        var plan = request.getPlan()
                .setId(flightService.getNextFlightId());

        var flight = flightService.startFlight(TestFlightMapper.toPlan(plan));
        return carrierService.getCarriers()
                .flatMap(carrier -> carrierService.start(carrier, flight))
                .collectList()
                .map(flights -> new FlightsStartedResponse()
                        .setFlights(flights));
    }

    @PostMapping("/atc/flights/{carrierId}/applications/{appId}/start")
    public Mono<FlightsStartedResponse> startApplicationOnCarrierById(
            @PathVariable("carrierId") String carrierId,
            @PathVariable("appId") String appId,
            @RequestBody StartFlightRequest request) {
        log.debug("start application: {} on carrier: {}", appId, carrierId);

        var plan = request.getPlan()
                .setId(flightService.getNextFlightId());

        var flight = flightService.startFlight(TestFlightMapper.toPlan(plan));

        return carrierService.getCarrier(carrierId)
                .flatMap(carrier -> carrierService.start(carrier, flight))
                .map(started -> new FlightsStartedResponse()
                        .setFlights(List.of(started)));
    }


    private FlightDto toFlight(Flight flight) {
        Map<String, FlightProgressDto> all = new HashMap<>();

        flight.getProgress().forEach((carrierId, data) -> {

            List<FlightRecordDto> records = data.getRecords().stream()
                    .map(this::toFlightRecord)
                    .collect(Collectors.toList());

            FlightRecordDto actual = toFlightRecord(data.getActual());

            FlightProgressDto dto = new FlightProgressDto()
                    .setCarrierId(carrierId)
                    .setRecords(records)
                    .setActual(actual);

            all.put(carrierId, dto);
        });

        return new FlightDto()
                .setId(flight.getPlan().getFlightId())
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
                .setState(record.getState())
                .setHistograms(record.getHistograms());
    }
}
