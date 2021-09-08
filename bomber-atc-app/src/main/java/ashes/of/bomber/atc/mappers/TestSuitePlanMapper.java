package ashes.of.bomber.atc.mappers;

import ashes.of.bomber.carrier.dto.flight.TestSuitePlanDto;
import ashes.of.bomber.flight.TestSuitePlan;

import java.util.stream.Collectors;

public class TestSuitePlanMapper {


    public static TestSuitePlan toPlan(TestSuitePlanDto testSuite) {
        var testCases = testSuite.getTestCases()
                .stream()
                .map(TestCasePlanMapper::toPlan)
                .collect(Collectors.toList());

        return new TestSuitePlan(testSuite.getName(), testCases);
    }

    public static TestSuitePlanDto toDto(TestSuitePlan testSuite) {
        var testCases = testSuite.getTestCases()
                .stream()
                .map(TestCasePlanMapper::toDto)
                .collect(Collectors.toList());

        return new TestSuitePlanDto()
                .setName(testSuite.getName())
                .setTestCases(testCases);
    }
}
