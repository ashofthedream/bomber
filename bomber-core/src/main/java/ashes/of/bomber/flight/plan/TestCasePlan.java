package ashes.of.bomber.flight.plan;

import ashes.of.bomber.configuration.Configuration;

import javax.annotation.Nullable;

public record TestCasePlan(String name, @Nullable Configuration configuration) {
}
