package ashes.of.bomber.carrier.controllers;

import ashes.of.bomber.Bomber;
import ashes.of.bomber.carrier.dto.requests.FlightStartedResponse;
import ashes.of.bomber.carrier.dto.requests.GetApplicationsResponse;
import ashes.of.bomber.carrier.dto.requests.StartFlightRequest;
import ashes.of.bomber.carrier.mappers.TestFlightMapper;
import ashes.of.bomber.carrier.mappers.TestAppMapper;
import ashes.of.bomber.flight.plan.TestFlightPlan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;


@RestController
@RequestMapping
public class ApplicationController {
    private static final Logger log = LogManager.getLogger();

    private final Bomber bomber;

    public ApplicationController(Bomber bomber) {
        this.bomber = bomber;
    }

    @GetMapping("/carrier/applications")
    public Mono<GetApplicationsResponse> getApplications() {
        log.debug("get applications");

        var apps = bomber.getApps()
                .stream()
                .map(TestAppMapper::toDto)
                .collect(Collectors.toList());


        var response = new GetApplicationsResponse()
                .setTestApps(apps);

        return Mono.just(response);
    }

    @PostMapping("/carrier/applications/start")
    public Mono<FlightStartedResponse> start(@RequestBody StartFlightRequest request) {
        log.info("start all applications by request: {}", request);
        TestFlightPlan plan = TestFlightMapper.toPlan(request.getFlight());

        bomber.startAsync(plan);

        return Mono.just(new FlightStartedResponse()
                .setId(plan.getFlightId()));
    }

    @PostMapping("/carrier/applications/stop")
    public ResponseEntity<?> stop() {
        log.info("stop application");
        bomber.stop();

        return ResponseEntity.ok().build();
    }
}
