package ashes.of.bomber.carrier.starter.controllers;

import ashes.of.bomber.carrier.dto.ApplicationDto;
import ashes.of.bomber.carrier.dto.FlightStartedDto;
import ashes.of.bomber.carrier.dto.SettingsDto;
import ashes.of.bomber.carrier.dto.flight.TestCasePlanDto;
import ashes.of.bomber.carrier.dto.flight.TestSuitePlanDto;
import ashes.of.bomber.carrier.dto.requests.StartFlightRequest;
import ashes.of.bomber.carrier.starter.mappers.ApplicationMapper;
import ashes.of.bomber.descriptions.TestCaseDescription;
import ashes.of.bomber.descriptions.TestSuiteDescription;
import ashes.of.bomber.flight.FlightPlan;
import ashes.of.bomber.flight.Settings;
import ashes.of.bomber.flight.SettingsBuilder;
import ashes.of.bomber.flight.TestCasePlan;
import ashes.of.bomber.flight.TestSuitePlan;
import ashes.of.bomber.runner.TestApp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
                .setState(ApplicationMapper.toDto(desc.getState()))
                .setTestSuites(testSuites(desc.getTestSuites()));

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/applications/start")
    public ResponseEntity<FlightStartedDto> start(@RequestBody StartFlightRequest request) {
        log.info("start all applications");
        FlightPlan plan = toPlan(request);

        app.startAsync();
//        app.startAsync(plan);

        return ResponseEntity.ok(new FlightStartedDto()
                .setId(plan.getFlightId()));
    }

    @PostMapping("/applications/stop")
    public ResponseEntity<?> stop() {
        log.info("stop application");
        app.stop();

        return ResponseEntity.ok().build();
    }

    private FlightPlan toPlan(StartFlightRequest request) {
        var testSuites = request.getPlan().getTestSuites()
                .stream()
                .map(testSuite -> {
                    var testCases = testSuite.getTestCases()
                            .stream()
                            .map(testCase -> new TestCasePlan(
                                    testCase.getName(),
                                    toSettings(testCase.getConfiguration().getWarmUp()),
                                    toSettings(testCase.getConfiguration().getSettings())
                            ))
                            .collect(Collectors.toList());

                    return new TestSuitePlan(testSuite.getName(), testCases);
                })
                .collect(Collectors.toList());

        return new FlightPlan(request.getPlan().getId(), testSuites);
    }

    @Nullable
    private Settings toSettings(@Nullable SettingsDto settings) {
        if (settings == null)
            return null;

        return new SettingsBuilder()
                .setDisabled(settings.isDisabled())
                .setTime(settings.getDuration(), TimeUnit.MILLISECONDS)
                .setThreadsCount(settings.getThreadsCount())
                .setThreadIterationsCount(settings.getThreadIterationsCount())
                .setTotalIterationsCount(settings.getTotalIterationsCount())
                .build();
    }

    private List<TestSuitePlanDto> testSuites(List<TestSuiteDescription> testSuites) {
        return testSuites.stream()
                .map(this::testSuite)
                .collect(Collectors.toList());
    }

    private TestSuitePlanDto testSuite(TestSuiteDescription suite) {
        return new TestSuitePlanDto()
                .setName(suite.getName())
                .setTestCases(testCases(suite));
    }

    private List<TestCasePlanDto> testCases(TestSuiteDescription suite) {
        return suite.getTestCases().stream()
                .map(this::testCase)
                .collect(Collectors.toList());
    }

    private TestCasePlanDto testCase(TestCaseDescription testCase) {
        return new TestCasePlanDto()
                .setName(testCase.getName());
    }
}
