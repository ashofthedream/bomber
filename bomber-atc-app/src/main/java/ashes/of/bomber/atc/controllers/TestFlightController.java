package ashes.of.bomber.atc.controllers;

import ashes.of.bomber.carrier.dto.TestFlightDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/atc/flights")
public class TestFlightController {


    @RequestMapping
    public Mono<List<TestFlightDto>> getFlights() {
        return Mono.justOrEmpty(new ArrayList<>());
    }

    @RequestMapping("/flights/active")
    public Mono<TestFlightDto> getActiveFlight() {
        return Mono.empty();
    }
}
