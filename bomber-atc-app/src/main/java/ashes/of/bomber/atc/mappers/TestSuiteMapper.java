package ashes.of.bomber.atc.mappers;

import ashes.of.bomber.carrier.dto.TestSuiteDto;
import ashes.of.bomber.flight.TestSuitePlan;

import java.util.stream.Collectors;

public class TestSuiteMapper {


    public static TestSuitePlan toPlan(TestSuiteDto testSuite) {
        var testCases = testSuite.getTestCases()
                .stream()
                .map(TestCaseMapper::toPlan)
                .collect(Collectors.toList());

        return new TestSuitePlan(testSuite.getName(), testCases);
    }

    public static TestSuiteDto toDto(TestSuitePlan testSuite) {
        var testCases = testSuite.getTestCases()
                .stream()
                .map(TestCaseMapper::toDto)
                .collect(Collectors.toList());

        return new TestSuiteDto()
                .setName(testSuite.getName())
                .setTestCases(testCases);
    }
}
