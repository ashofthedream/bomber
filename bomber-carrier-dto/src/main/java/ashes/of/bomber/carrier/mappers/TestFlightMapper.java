package ashes.of.bomber.carrier.mappers;

import ashes.of.bomber.carrier.dto.flight.FlightSnapshotDto;
import ashes.of.bomber.carrier.dto.flight.TestFlightDto;
import ashes.of.bomber.carrier.dto.flight.WorkerSnapshotDto;
import ashes.of.bomber.flight.plan.TestFlightPlan;
import ashes.of.bomber.snapshots.TestFlightSnapshot;
import ashes.of.bomber.snapshots.WorkerSnapshot;

import javax.annotation.Nullable;
import java.util.stream.Collectors;

public class TestFlightMapper {

    public static TestFlightPlan toPlan(TestFlightDto flight) {
        var testApps = flight.getTestApps()
                .stream()
                .map(TestAppMapper::toPlan)
                .collect(Collectors.toList());

        return new TestFlightPlan(flight.getId(), testApps);
    }

    public static TestFlightDto toDto(TestFlightPlan flight) {
        var testApps = flight.getTestApps()
                .stream()
                .map(TestAppMapper::toDto)
                .collect(Collectors.toList());

        return new TestFlightDto()
                .setId(flight.getFlightId())
                .setTestApps(testApps);
    }

    @Nullable
    public static FlightSnapshotDto toDto(@Nullable TestFlightSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }

        return new FlightSnapshotDto()
                .setSettings(SettingsMapper.toDto(snapshot.getSettings()))
                .setTestApp(snapshot.getCurrent())
                .setTestSuite(snapshot.getTestSuite())
                .setTestCase(snapshot.getTestCase())
                .setTestSuiteStart(snapshot.getTestSuiteStartTime().toEpochMilli())
                .setTestCaseStart(snapshot.getTestCaseStartTime().toEpochMilli())
                .setElapsedTime(snapshot.getCaseElapsedTime())
                .setRemainTime(snapshot.getCaseRemainTime())
                .setRemainTotalIterations(snapshot.getRemainIterationsCount())
                .setErrorsCount(snapshot.getErrorCount())
                .setWorkers(snapshot.getWorkers().stream()
                        .map(TestFlightMapper::toDto)
                        .collect(Collectors.toList()));
    }

    public static WorkerSnapshotDto toDto(WorkerSnapshot state) {
        return new WorkerSnapshotDto()
                .setName(state.getWorker())
                .setIterationsCount(state.getCurrentIterationsCount())
                .setRemainIterationsCount(state.getRemainIterationsCount())
                .setErrorsCount(state.getErrorsCount());
    }
}
