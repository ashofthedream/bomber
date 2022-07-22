package ashes.of.bomber.cli.config;

import javax.annotation.Nullable;

public record TestCaseConfig(String name, boolean async, @Nullable ConfigurationConfig configuration, HttpRequestConfig http) {

}
