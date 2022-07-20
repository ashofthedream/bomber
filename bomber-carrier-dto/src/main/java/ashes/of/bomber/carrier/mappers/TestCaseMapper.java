package ashes.of.bomber.carrier.mappers;

import ashes.of.bomber.carrier.dto.flight.TestCaseDto;
import ashes.of.bomber.core.TestCase;
import ashes.of.bomber.flight.plan.TestCasePlan;

import java.util.Optional;

public class TestCaseMapper {

    public static TestCasePlan toPlan(TestCaseDto testCase) {
        var config = Optional.ofNullable(testCase.getConfiguration())
                .map(ConfigurationMapper::toConfiguration)
                .orElse(null);

        return new TestCasePlan(testCase.getName(), config);
    }

    public static TestCaseDto toDto(TestCasePlan testCase) {
        var config = Optional.ofNullable(testCase.configuration())
                .map(ConfigurationMapper::toDto)
                .orElse(null);


        return new TestCaseDto()
                .setName(testCase.name())
                .setConfiguration(config);
    }


    public static <T> TestCaseDto toDto(TestCase<T> testCase) {
        var config = Optional.ofNullable(testCase.getConfiguration())
                .map(ConfigurationMapper::toDto)
                .orElse(null);


        return new TestCaseDto()
                .setName(testCase.getName())
                .setConfiguration(config);
    }
}
