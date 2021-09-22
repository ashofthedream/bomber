package ashes.of.bomber.carrier.mappers;

import ashes.of.bomber.carrier.dto.flight.TestAppSnapshotDto;
import ashes.of.bomber.carrier.dto.flight.TestCaseSnapshotDto;
import ashes.of.bomber.carrier.dto.flight.TestFlightSnapshotDto;
import ashes.of.bomber.carrier.dto.flight.TestFlightDto;
import ashes.of.bomber.carrier.dto.flight.TestSuiteSnapshotDto;
import ashes.of.bomber.carrier.dto.flight.WorkerSnapshotDto;
import ashes.of.bomber.flight.plan.TestFlightPlan;
import ashes.of.bomber.snapshots.TestAppSnapshot;
import ashes.of.bomber.snapshots.TestCaseSnapshot;
import ashes.of.bomber.snapshots.TestFlightSnapshot;
import ashes.of.bomber.snapshots.TestSuiteSnapshot;
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
    public static TestFlightSnapshotDto toDto(@Nullable TestFlightSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }

        return new TestFlightSnapshotDto()
                .setPlan(TestFlightMapper.toDto(snapshot.getPlan()))
                .setCurrent(toDto(snapshot.getCurrent()))
                .setWorkers(snapshot.getWorkers().stream()
                        .map(TestFlightMapper::toDto)
                        .collect(Collectors.toList()));
    }

    @Nullable
    public static TestAppSnapshotDto toDto(@Nullable TestAppSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }

        return new TestAppSnapshotDto()
                .setName(snapshot.getName())
                .setCurrent(toDto(snapshot.getCurrent()));
    }

    @Nullable
    public static TestSuiteSnapshotDto toDto(@Nullable TestSuiteSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }

        return new TestSuiteSnapshotDto()
                .setName(snapshot.getName())
                .setCurrent(toDto(snapshot.getCurrent()));
    }

    @Nullable
    public static TestCaseSnapshotDto toDto(@Nullable TestCaseSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }

        return new TestCaseSnapshotDto()
                .setName(snapshot.getName())
                .setSettings(SettingsMapper.toDto(snapshot.getSettings()))
                .setStartTime(snapshot.getStartTime().toEpochMilli())
                .setFinishTime(snapshot.getFinishTime() != null ? snapshot.getFinishTime().toEpochMilli() : null)
                .setCurrentIterationsCount(snapshot.getCurrentIterationsCount())
                .setErrorsCount(snapshot.getErrorsCount());
    }

    public static WorkerSnapshotDto toDto(WorkerSnapshot state) {
        return new WorkerSnapshotDto()
                .setName(state.getWorker())
                .setIterationsCount(state.getCurrentIterationsCount())
                .setRemainIterationsCount(state.getRemainIterationsCount())
                .setErrorsCount(state.getErrorsCount());
    }
}
