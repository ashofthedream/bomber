package ashes.of.bomber.cli.config;

import java.util.List;

public record BomberConfig(List<String> sinks,
                           List<String> watchers,
                           ConfigurationConfig configuration,
                           List<TestCaseConfig> testCases) {
}
