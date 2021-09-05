package ashes.of.bomber.carrier.starter.mapping;

import ashes.of.bomber.carrier.dto.ApplicationStateDto;
import ashes.of.bomber.carrier.dto.WorkerStateDto;
import ashes.of.bomber.descriptions.TestAppStateDescription;
import ashes.of.bomber.descriptions.WorkerDescription;

import java.util.stream.Collectors;

public class ApplicationStateMapper {

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
                        .map(ApplicationStateMapper::toDto)
                        .collect(Collectors.toList()));
    }

    public static WorkerStateDto toDto(WorkerDescription state) {
        return new WorkerStateDto()
                .setName(state.getWorker())
                .setIterationsCount(state.getCurrentIterationsCount())
                .setRemainIterationsCount(state.getRemainIterationsCount())
                .setErrorsCount(state.getErrorsCount());
    }
}
