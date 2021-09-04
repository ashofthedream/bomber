package ashes.of.bomber.carrier.starter.controllers;

import ashes.of.bomber.carrier.dto.ApplicationDto;
import ashes.of.bomber.carrier.dto.ApplicationStateDto;
import ashes.of.bomber.carrier.dto.FlightStartedDto;
import ashes.of.bomber.carrier.dto.SettingsDto;
import ashes.of.bomber.carrier.dto.TestCaseDto;
import ashes.of.bomber.carrier.dto.TestSuiteDto;
import ashes.of.bomber.carrier.dto.WorkerStateDto;
import ashes.of.bomber.carrier.dto.requests.StartFlightRequest;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.descriptions.TestAppStateDescription;
import ashes.of.bomber.descriptions.TestCaseDescription;
import ashes.of.bomber.descriptions.TestSuiteDescription;
import ashes.of.bomber.descriptions.WorkerStateDescription;
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
@RequestMapping("/applications")
public class ApplicationController {
    private static final Logger log = LogManager.getLogger();

    private final TestApp app;

    public ApplicationController(TestApp app) {
        this.app = app;
    }

    @GetMapping
    public ResponseEntity<ApplicationDto> getApplications() {
        log.debug("get applications");

        ApplicationDto dto = new ApplicationDto()
                .setName(app.getName())
                .setState(state(app.getState()))
                .setTestSuites(testSuites(app.getTestSuites()));

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/start")
    public ResponseEntity<FlightStartedDto> start(@RequestBody StartFlightRequest start) {
        log.info("start all applications");
        app.startAsync(app.creteDefaultPlan(start.getFlightId()));

        return ResponseEntity.ok(new FlightStartedDto()
                .setId(start.getFlightId()));
    }

    @PostMapping("/stop")
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
                .setLoadTest(settings(suite.getSettings()))
                .setWarmUp(settings(suite.getWarmUp()))
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

    private SettingsDto settings(Settings settings) {
        return new SettingsDto()
                .setDisabled(settings.isDisabled())
                .setDuration(settings.getTime().toMillis())
                .setThreadsCount(settings.getThreadsCount())
                .setThreadIterationsCount(settings.getThreadIterationsCount())
                .setTotalIterationsCount(settings.getTotalIterationsCount());
    }

    private ApplicationStateDto state(TestAppStateDescription state) {
        return new ApplicationStateDto()
                .setStage(state.getStage().name())
                .setSettings(settings(state.getSettings()))
                .setTestSuite(state.getTestSuite())
                .setTestCase(state.getTestCase())
                .setTestSuiteStart(state.getTestSuiteStartTime().toEpochMilli())
                .setTestCaseStart(state.getTestCaseStartTime().toEpochMilli())
                .setElapsedTime(state.getCaseElapsedTime())
                .setRemainTime(state.getCaseRemainTime())
                .setRemainTotalIterations(state.getRemainIterationsCount())
                .setErrorsCount(state.getErrorCount())
                .setWorkers(state.getWorkersState().stream()
                        .map(this::workerState)
                        .collect(Collectors.toList()));
    }

    private WorkerStateDto workerState(WorkerStateDescription state) {
        return new WorkerStateDto()
                .setName(state.getWorker())
                .setIterationsCount(state.getCurrentIterationCount())
                .setRemainIterationsCount(state.getRemainIterationsCount())
                .setErrorsCount(state.getErrorsCount());
    }
}
