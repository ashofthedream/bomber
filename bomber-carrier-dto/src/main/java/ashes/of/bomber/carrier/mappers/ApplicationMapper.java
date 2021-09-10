package ashes.of.bomber.carrier.mappers;

import ashes.of.bomber.carrier.dto.ApplicationDto;
import ashes.of.bomber.carrier.dto.ApplicationStateDto;
import ashes.of.bomber.carrier.dto.WorkerStateDto;
import ashes.of.bomber.descriptions.TestAppDescription;
import ashes.of.bomber.descriptions.TestAppStateDescription;
import ashes.of.bomber.descriptions.WorkerDescription;

import java.util.stream.Collectors;

public class ApplicationMapper {

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
                        .map(ApplicationMapper::toDto)
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
}
