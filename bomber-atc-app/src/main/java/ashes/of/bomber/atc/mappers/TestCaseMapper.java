package ashes.of.bomber.atc.mappers;

import ashes.of.bomber.carrier.dto.TestCaseDto;
import ashes.of.bomber.flight.TestCasePlan;

public class TestCaseMapper {

    public static TestCasePlan toPlan(TestCaseDto testCase) {
        return new TestCasePlan(
                testCase.getName(),
                SettingsMapper.toSettingsOrNull(testCase.getWarmUp()),
                SettingsMapper.toSettingsOrNull(testCase.getLoadTest())
        );
    }

    public static TestCaseDto toDto(TestCasePlan testCase) {
        return new TestCaseDto()
                .setName(testCase.getName())
                .setWarmUp(SettingsMapper.toDto(testCase.getWarmUp()))
                .setLoadTest(SettingsMapper.toDto(testCase.getSettings()));
    }
}
