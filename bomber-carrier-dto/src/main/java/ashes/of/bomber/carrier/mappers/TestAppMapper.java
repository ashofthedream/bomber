package ashes.of.bomber.carrier.mappers;

import ashes.of.bomber.carrier.dto.ApplicationDto;
import ashes.of.bomber.carrier.dto.ApplicationStateDto;
import ashes.of.bomber.carrier.dto.WorkerStateDto;
import ashes.of.bomber.carrier.dto.flight.TestAppDto;
import ashes.of.bomber.descriptions.TestAppDescription;
import ashes.of.bomber.descriptions.TestAppStateDescription;
import ashes.of.bomber.descriptions.WorkerDescription;
import ashes.of.bomber.flight.TestAppPlan;

import java.util.stream.Collectors;

public class TestAppMapper {

    public static ApplicationStateDto toDto(TestAppStateDescription state) {
        return new ApplicationStateDto()
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

    public static WorkerStateDto toDto(WorkerDescription state) {
        return new WorkerStateDto()
                .setName(state.getWorker())
                .setIterationsCount(state.getCurrentIterationsCount())
                .setRemainIterationsCount(state.getRemainIterationsCount())
                .setErrorsCount(state.getErrorsCount());
    }

    public static ApplicationDto toDto(TestAppDescription desc) {
        var testSuites = desc.getTestSuites().stream()
                .map(TestSuiteMapper::toDto)
                .collect(Collectors.toList());

        return new ApplicationDto()
                .setName(desc.getName())
                .setState(toDto(desc.getState()))
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
