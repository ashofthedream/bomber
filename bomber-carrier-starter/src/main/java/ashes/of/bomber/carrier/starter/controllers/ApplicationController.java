package ashes.of.bomber.carrier.starter.controllers;

import ashes.of.bomber.carrier.dto.*;
import ashes.of.bomber.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/applications")
public class ApplicationController {
    private static final Logger log = LogManager.getLogger();

    private final BomberApp app;

    public ApplicationController(BomberApp app) {
        this.app = app;
    }

    @GetMapping
    public ResponseEntity<ApplicationDto> getApplications() {
        log.debug("get applications");
        ApplicationDto dto = ApplicationDto.builder()
                .name(app.getName())
                .state(state(app.getState()))
                .testSuites(testSuites(app.getTestSuites()))
                .build();

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/start")
    public ResponseEntity<?> startAllApp() {
        log.info("start all applications");
        app.startAsync();

        return ResponseEntity.ok().build();
    }

    @PostMapping("/stop")
    public ResponseEntity<?> stopAllApp() {
        log.info("stop application");
        app.stop();

        return ResponseEntity.ok().build();
    }

    private List<TestSuiteDto> testSuites(List<TestSuiteModel> testSuites) {
        return testSuites.stream()
                .map(this::testSuite)
                .collect(Collectors.toList());
    }

    private TestSuiteDto testSuite(TestSuiteModel suite) {
        return TestSuiteDto.builder()
                .loadTest(settings(suite.getSettings()))
                .warmUp(settings(suite.getWarmUp()))
                .name(suite.getName())
                .testCases(testCases(suite))
                .build();
    }

    private List<TestCaseDto> testCases(TestSuiteModel suite) {
        return suite.getTestCases().stream()
                .map(this::testCase)
                .collect(Collectors.toList());
    }

    private TestCaseDto testCase(TestCaseModel testCase) {
        return TestCaseDto.builder()
                .name(testCase.getName())
                .build();
    }

    private SettingsDto settings(Settings settings) {
        return SettingsDto.builder()
                .disabled(settings.isDisabled())
                .duration(settings.getTime().toMillis())
                .threadsCount(settings.getThreadsCount())
                .threadIterationsCount(settings.getThreadIterationsCount())
                .totalIterationsCount(settings.getTotalIterationsCount())
                .build();
    }

    private ApplicationStateDto state(State state) {
        return ApplicationStateDto.builder()
                .stage(state.getStage().name())
                .settings(settings(state.getSettings()))
                .testSuite(state.getTestSuite())
                .testCase(state.getTestCase())
                .testSuiteStart(state.getTestSuiteStartTime().toEpochMilli())
                .testCaseStart(state.getTestCaseStartTime().toEpochMilli())
                .elapsedTime(state.getCaseElapsedTime())
                .remainTime(state.getCaseRemainTime())
                .errorsCount(state.getErrorCount())
                .workers(Collections.emptyList())
                .build();
    }
}
