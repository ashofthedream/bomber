package ashes.of.bomber.carrier.mappers;

import ashes.of.bomber.carrier.dto.flight.ConfigurationDto;
import ashes.of.bomber.carrier.dto.flight.TestCaseDto;
import ashes.of.bomber.descriptions.ConfigurationDescription;
import ashes.of.bomber.descriptions.TestCaseDescription;
import ashes.of.bomber.flight.TestCasePlan;

import java.util.Optional;

public class TestCaseMapper {

    public static TestCasePlan toPlan(TestCaseDto testCase) {
        var config = Optional.ofNullable(testCase.getConfiguration())
                .map(dto -> new ConfigurationDescription(
                        SettingsMapper.toSettingsOrNull(dto.getWarmUp()),
                        SettingsMapper.toSettingsOrNull(dto.getSettings()))

                )
                .orElse(null);

        return new TestCasePlan(testCase.getName(), config);
    }

    public static TestCaseDto toDto(TestCasePlan testCase) {
        var config = Optional.ofNullable(testCase.getConfiguration())
                .map(TestCaseMapper::toConfigurationDto)
                .orElse(null);


        return new TestCaseDto()
                .setName(testCase.getName())
                .setConfiguration(config);
    }

    public static TestCaseDto toDto(TestCaseDescription testCase) {
        var config = Optional.ofNullable(testCase.getConfiguration())
                .map(TestCaseMapper::toConfigurationDto)
                .orElse(null);


        return new TestCaseDto()
                .setName(testCase.getName())
                .setConfiguration(config);
    }

    public static ConfigurationDto toConfigurationDto(ConfigurationDescription desc) {
        return new ConfigurationDto()
                .setSettings(SettingsMapper.toDto(desc.getSettings()))
                .setWarmUp(SettingsMapper.toDto(desc.getWarmUp()));
    }
}
