package ashes.of.bomber.snapshots;

import javax.annotation.Nullable;

public record TestSuiteSnapshot(String name, @Nullable TestCaseSnapshot current) {
}
