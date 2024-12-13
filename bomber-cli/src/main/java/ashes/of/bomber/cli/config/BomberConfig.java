package ashes.of.bomber.cli.config;

import ashes.of.bomber.configuration.dto.ConfigurationDto;

import java.util.List;

public record BomberConfig(List<String> sinks,
                           List<String> watchers,
                           ConfigurationDto configuration,
                           List<TestCaseConfig> testCases) {
}
