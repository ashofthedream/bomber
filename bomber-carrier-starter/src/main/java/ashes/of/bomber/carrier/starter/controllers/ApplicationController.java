package ashes.of.bomber.carrier.starter.controllers;

import ashes.of.bomber.carrier.dto.ApplicationDto;
import ashes.of.bomber.carrier.dto.FlightStartedDto;
import ashes.of.bomber.carrier.dto.TestCaseDto;
import ashes.of.bomber.carrier.dto.TestSuiteDto;
import ashes.of.bomber.carrier.dto.requests.StartFlightRequest;
import ashes.of.bomber.carrier.starter.mapping.ApplicationStateMapper;
import ashes.of.bomber.carrier.starter.mapping.SettingsMapper;
import ashes.of.bomber.descriptions.TestCaseDescription;
import ashes.of.bomber.descriptions.TestSuiteDescription;
import ashes.of.bomber.runner.TestApp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping
public class ApplicationController {
    private static final Logger log = LogManager.getLogger();

    private final TestApp app;

    public ApplicationController(TestApp app) {
        this.app = app;
    }

    @GetMapping("/applications")
    public ResponseEntity<ApplicationDto> getApplications() {
        log.debug("get applications");
        var desc = app.getDescription();

        ApplicationDto dto = new ApplicationDto()
                .setName(desc.getName())
                .setState(ApplicationStateMapper.toDto(desc.getState()))
                .setTestSuites(testSuites(desc.getTestSuites()));

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/applications/start")
    public ResponseEntity<FlightStartedDto> start(@RequestBody StartFlightRequest start) {
        log.info("start all applications");
        app.startAsync(app.creteDefaultPlan(start.getFlightId()));

        return ResponseEntity.ok(new FlightStartedDto()
                .setId(start.getFlightId()));
    }

    @PostMapping("/applications/stop")
    public ResponseEntity<?> stop() {
        log.info("stop application");
        app.stop();

        return ResponseEntity.ok().build();
    }



    private List<TestSuiteDto> testSuites(List<TestSuiteDescription> testSuites) {
        return testSuites.stream()
                .map(this::testSuite)
                .collect(Collectors.toList());
    }

    private TestSuiteDto testSuite(TestSuiteDescription suite) {
        return new TestSuiteDto()
                .setLoadTest(SettingsMapper.toDto(suite.getSettings()))
                .setWarmUp(SettingsMapper.toDto(suite.getWarmUp()))
                .setName(suite.getName())
                .setTestCases(testCases(suite));
    }

    private List<TestCaseDto> testCases(TestSuiteDescription suite) {
        return suite.getTestCases().stream()
                .map(this::testCase)
                .collect(Collectors.toList());
    }

    private TestCaseDto testCase(TestCaseDescription testCase) {
        return new TestCaseDto()
                .setName(testCase.getName());
    }





}
