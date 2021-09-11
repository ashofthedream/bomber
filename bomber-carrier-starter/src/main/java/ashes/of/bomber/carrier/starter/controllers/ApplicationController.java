package ashes.of.bomber.carrier.starter.controllers;

import ashes.of.bomber.carrier.dto.ApplicationDto;
import ashes.of.bomber.carrier.dto.requests.FlightStartedResponse;
import ashes.of.bomber.carrier.dto.requests.StartFlightRequest;
import ashes.of.bomber.carrier.mappers.TestFlightMapper;
import ashes.of.bomber.carrier.mappers.TestAppMapper;
import ashes.of.bomber.flight.TestFlightPlan;
import ashes.of.bomber.runner.TestApp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping
public class ApplicationController {
    private static final Logger log = LogManager.getLogger();

    private final TestApp app;

    public ApplicationController(TestApp app) {
        this.app = app;
    }

    @GetMapping("/carrier/applications")
    public ResponseEntity<ApplicationDto> getApplications() {
        log.debug("get applications");
        var desc = app.getDescription();


        return ResponseEntity.ok(TestAppMapper.toDto(app.getDescription()));
    }

    @PostMapping("/carrier/applications/start")
    public ResponseEntity<FlightStartedResponse> start(@RequestBody StartFlightRequest request) {
        log.info("start all applications by request: {}", request);
        TestFlightPlan plan = TestFlightMapper.toPlan(request.getFlight());

        app.startAsync(plan);

        return ResponseEntity.ok(new FlightStartedResponse()
                .setId(plan.getFlightId()));
    }

    @PostMapping("/carrier/applications/stop")
    public ResponseEntity<?> stop() {
        log.info("stop application");
        app.stop();

        return ResponseEntity.ok().build();
    }

}
