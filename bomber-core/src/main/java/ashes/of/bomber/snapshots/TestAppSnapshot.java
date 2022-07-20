package ashes.of.bomber.snapshots;

import javax.annotation.Nullable;

public record TestAppSnapshot(String name, @Nullable TestSuiteSnapshot current) {
}
