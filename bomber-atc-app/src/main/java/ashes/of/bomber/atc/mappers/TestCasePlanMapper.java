package ashes.of.bomber.atc.mappers;

import ashes.of.bomber.carrier.dto.ConfigurationDto;
import ashes.of.bomber.carrier.dto.flight.TestCasePlanDto;
import ashes.of.bomber.flight.TestCasePlan;

public class TestCasePlanMapper {

    public static TestCasePlan toPlan(TestCasePlanDto testCase) {
        return new TestCasePlan(
                testCase.getName(),
                SettingsMapper.toSettingsOrNull(testCase.getConfiguration().getWarmUp()),
                SettingsMapper.toSettingsOrNull(testCase.getConfiguration().getSettings())
        );
    }

    public static TestCasePlanDto toDto(TestCasePlan testCase) {
        return new TestCasePlanDto()
                .setName(testCase.getName())
                .setConfiguration(new ConfigurationDto()
                        .setSettings(SettingsMapper.toDto(testCase.getSettings()))
                        .setWarmUp(SettingsMapper.toDto(testCase.getWarmUp()))
                );
    }
}
