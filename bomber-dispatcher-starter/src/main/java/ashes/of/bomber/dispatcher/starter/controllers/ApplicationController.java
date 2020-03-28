package ashes.of.bomber.dispatcher.starter.controllers;

import ashes.of.bomber.core.BomberApp;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.TestCaseModel;
import ashes.of.bomber.core.TestSuiteModel;
import ashes.of.bomber.dispatcher.dto.ApplicationDto;
import ashes.of.bomber.dispatcher.dto.SettingsDto;
import ashes.of.bomber.dispatcher.dto.TestCaseDto;
import ashes.of.bomber.dispatcher.dto.TestSuiteDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/application")
public class ApplicationController {
    private static final Logger log = LogManager.getLogger();

    private final BomberApp app;

    public ApplicationController(BomberApp app) {
        this.app = app;
    }

    @GetMapping
    public ResponseEntity<ApplicationDto> info() {
        List<TestSuiteModel> testSuites = app.getTestSuites();
        ApplicationDto dto = ApplicationDto.builder()
                .name(app.getName())
                .testSuites(testSuites(testSuites))
                .build();

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/run")
    public ResponseEntity<?> run() {
        log.info("run");
        this.app.runAsync();

        return ResponseEntity.ok().build();
    }

    @PostMapping("/shutdown")
    public ResponseEntity<?> shutdown() {
        log.info("shutdown");
        this.app.shutdown();

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
