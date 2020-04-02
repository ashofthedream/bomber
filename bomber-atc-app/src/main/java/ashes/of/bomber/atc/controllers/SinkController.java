package ashes.of.bomber.atc.controllers;

import ashes.of.bomber.atc.services.FlightService;
import ashes.of.bomber.carrier.dto.events.SinkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/atc/sink")
public class SinkController {
    private static final Logger log = LogManager.getLogger();

    private final FlightService flightService;

    public SinkController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping
    public void sink(@RequestBody SinkEvent event) {
        log.debug("received event: event: {}", event);
        flightService.getActive()
//                .filter(flight -> flight.getId() == event.getFlightId())
                .subscribe(flight -> flight.event(event));

    }
}
