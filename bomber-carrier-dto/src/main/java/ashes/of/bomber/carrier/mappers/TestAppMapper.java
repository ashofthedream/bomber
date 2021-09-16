package ashes.of.bomber.carrier.mappers;

import ashes.of.bomber.carrier.dto.flight.TestAppDto;
import ashes.of.bomber.core.TestApp;
import ashes.of.bomber.flight.plan.TestAppPlan;

import java.util.stream.Collectors;

public class TestAppMapper {



    public static TestAppDto toDto(TestApp app) {
        var testSuites = app.getTestSuites().stream()
                .map(TestSuiteMapper::toDto)
                .collect(Collectors.toList());

        return new TestAppDto()
                .setName(app.getName())
                .setTestSuites(testSuites);
    }

    public static TestAppPlan toPlan(TestAppDto dto) {
        var testSuites = dto.getTestSuites()
                .stream()
                .map(TestSuiteMapper::toPlan)
                .collect(Collectors.toList());

        return new TestAppPlan(dto.getName(), testSuites);
    }

    public static TestAppDto toDto(TestAppPlan plan) {
        var testSuites = plan.getTestSuites()
                .stream()
                .map(TestSuiteMapper::toDto)
                .collect(Collectors.toList());

        return new TestAppDto()
                .setName(plan.getName())
                .setTestSuites(testSuites);
    }
}
