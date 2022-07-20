package ashes.of.bomber.snapshots;

import ashes.of.bomber.configuration.Settings;

import javax.annotation.Nullable;
import java.time.Instant;

public record TestCaseSnapshot(String name, Settings settings, Instant startTime, @Nullable Instant finishTime, long currentIterationsCount, long errorsCount) {

}
