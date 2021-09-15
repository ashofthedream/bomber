package ashes.of.bomber.carrier.mappers;

import ashes.of.bomber.carrier.dto.flight.TestSuiteDto;
import ashes.of.bomber.core.TestSuite;
import ashes.of.bomber.plan.TestSuitePlan;

import java.util.stream.Collectors;

public class TestSuiteMapper {


    public static TestSuitePlan toPlan(TestSuiteDto testSuite) {
        var testCases = testSuite.getTestCases()
                .stream()
                .map(TestCaseMapper::toPlan)
                .collect(Collectors.toList());

        return new TestSuitePlan(testSuite.getName(), testCases);
    }

    public static <T> TestSuiteDto toDto(TestSuite<T> testSuite) {
        var testCases = testSuite.getTestCases()
                .stream()
                .map(TestCaseMapper::toDto)
                .collect(Collectors.toList());

        return new TestSuiteDto()
                .setName(testSuite.getName())
                .setTestCases(testCases);
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
