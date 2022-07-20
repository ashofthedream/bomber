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
        var testApps = flight.testApps()
                .stream()
                .map(TestAppMapper::toDto)
                .collect(Collectors.toList());

        return new TestFlightDto()
                .setId(flight.flightId())
                .setTestApps(testApps);
    }

    @Nullable
    public static TestFlightSnapshotDto toDto(@Nullable TestFlightSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }

        return new TestFlightSnapshotDto()
                .setPlan(TestFlightMapper.toDto(snapshot.plan()))
                .setCurrent(toDto(snapshot.current()))
                .setWorkers(snapshot.workers().stream()
                        .map(TestFlightMapper::toDto)
                        .collect(Collectors.toList()));
    }

    @Nullable
    public static TestAppSnapshotDto toDto(@Nullable TestAppSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }

        return new TestAppSnapshotDto()
                .setName(snapshot.name())
                .setCurrent(toDto(snapshot.current()));
    }

    @Nullable
    public static TestSuiteSnapshotDto toDto(@Nullable TestSuiteSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }

        return new TestSuiteSnapshotDto()
                .setName(snapshot.name())
                .setCurrent(toDto(snapshot.current()));
    }

    @Nullable
    public static TestCaseSnapshotDto toDto(@Nullable TestCaseSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }

        return new TestCaseSnapshotDto()
                .setName(snapshot.name())
                .setSettings(SettingsMapper.toDto(snapshot.settings()))
                .setStartTime(snapshot.startTime().toEpochMilli())
                .setFinishTime(snapshot.finishTime() != null ? snapshot.finishTime().toEpochMilli() : null)
                .setCurrentIterationsCount(snapshot.currentIterationsCount())
                .setErrorsCount(snapshot.errorsCount());
    }

    public static WorkerSnapshotDto toDto(WorkerSnapshot state) {
        return new WorkerSnapshotDto()
                .setName(state.worker())
                .setIterationsCount(state.currentIterationsCount())
                .setErrorsCount(state.errorsCount());
    }
}
