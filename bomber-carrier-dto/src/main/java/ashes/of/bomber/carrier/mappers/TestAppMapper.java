package ashes.of.bomber.carrier.mappers;

import ashes.of.bomber.carrier.dto.flight.AppStateDto;
import ashes.of.bomber.carrier.dto.flight.WorkerStateDto;
import ashes.of.bomber.carrier.dto.flight.TestAppDto;
import ashes.of.bomber.core.TestApp;
import ashes.of.bomber.snapshots.FlightSnapshot;
import ashes.of.bomber.snapshots.WorkerSnapshot;
import ashes.of.bomber.flight.plan.TestAppPlan;

import java.util.stream.Collectors;

public class TestAppMapper {

    public static AppStateDto toDto(FlightSnapshot state) {
        return new AppStateDto()
                .setStage(state.getStage().name())
                .setSettings(SettingsMapper.toDto(state.getSettings()))
                .setTestSuite(state.getTestSuite())
                .setTestCase(state.getTestCase())
                .setTestSuiteStart(state.getTestSuiteStartTime().toEpochMilli())
                .setTestCaseStart(state.getTestCaseStartTime().toEpochMilli())
                .setElapsedTime(state.getCaseElapsedTime())
                .setRemainTime(state.getCaseRemainTime())
                .setRemainTotalIterations(state.getRemainIterationsCount())
                .setErrorsCount(state.getErrorCount())
                .setWorkers(state.getWorkers().stream()
                        .map(TestAppMapper::toDto)
                        .collect(Collectors.toList()));
    }

    public static WorkerStateDto toDto(WorkerSnapshot state) {
        return new WorkerStateDto()
                .setName(state.getWorker())
                .setIterationsCount(state.getCurrentIterationsCount())
                .setRemainIterationsCount(state.getRemainIterationsCount())
                .setErrorsCount(state.getErrorsCount());
    }

    public static TestAppDto toDto(TestApp app) {
        var testSuites = app.getTestSuites().stream()
                .map(TestSuiteMapper::toDto)
                .collect(Collectors.toList());

        return new TestAppDto()
                .setName(app.getName())
                .setTestSuites(testSuites);
    }

    public static TestAppPlan toPlan(TestAppDto dto) {
        var testSuites = dto.getTestSuites()
                .stream()
                .map(TestSuiteMapper::toPlan)
                .collect(Collectors.toList());

        return new TestAppPlan(dto.getName(), testSuites);
    }

    public static TestAppDto toDto(TestAppPlan plan) {
        var testSuites = plan.getTestSuites()
                .stream()
                .map(TestSuiteMapper::toDto)
                .collect(Collectors.toList());

        return new TestAppDto()
                .setName(plan.getName())
                .setTestSuites(testSuites);
    }
}
