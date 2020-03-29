package ashes.of.bomber.carrier.starter.controllers;

import ashes.of.bomber.core.BomberApp;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.TestCaseModel;
import ashes.of.bomber.core.TestSuiteModel;
import ashes.of.bomber.carrier.dto.ApplicationDto;
import ashes.of.bomber.carrier.dto.SettingsDto;
import ashes.of.bomber.carrier.dto.TestCaseDto;
import ashes.of.bomber.carrier.dto.TestSuiteDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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
        List<TestSuiteModel> testSuites = app.getTestSuites();
        ApplicationDto dto = ApplicationDto.builder()
                .name(app.getName())
                .testSuites(testSuites(testSuites))
                .build();

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/start")
    public ResponseEntity<?> startAllApp() {
        log.info("start all applications");
        this.app.startAsync();

        return ResponseEntity.ok().build();
    }

    @PostMapping("/stop")
    public ResponseEntity<?> stopAllApp() {
        log.info("stop application");
        this.app.stop();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/state")
    public ResponseEntity<?> getState() {
        log.info("getState");

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
}
