package ashes.of.bomber.carrier.mappers;

import ashes.of.bomber.carrier.dto.flight.TestAppDto;
import ashes.of.bomber.core.TestSuiteGroup;
import ashes.of.bomber.flight.plan.TestAppPlan;

public class TestAppMapper {

    public static TestAppDto toDto(TestSuiteGroup app) {
        var testSuites = app.testSuites().stream()
                .map(TestSuiteMapper::toDto)
                .toList();

        return new TestAppDto()
                .setName(app.name())
                .setTestSuites(testSuites);
    }

    public static TestAppPlan toPlan(TestAppDto dto) {
        var testSuites = dto.getTestSuites()
                .stream()
                .map(TestSuiteMapper::toPlan)
                .toList();

        return new TestAppPlan(dto.getName(), testSuites);
    }

    public static TestAppDto toDto(TestAppPlan plan) {
        var testSuites = plan.testSuites()
                .stream()
                .map(TestSuiteMapper::toDto)
                .toList();

        return new TestAppDto()
                .setName(plan.name())
                .setTestSuites(testSuites);
    }
}
