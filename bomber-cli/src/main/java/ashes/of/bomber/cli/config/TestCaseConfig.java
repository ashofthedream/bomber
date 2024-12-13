package ashes.of.bomber.cli.config;

import ashes.of.bomber.configuration.dto.ConfigurationDto;

import javax.annotation.Nullable;

public record TestCaseConfig(String name, boolean async, @Nullable ConfigurationDto configuration, HttpRequestConfig http) {

}
