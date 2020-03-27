package ashes.of.bomber.dispatcher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestSuiteDto {
    private String name;
    private SettingsDto loadTestSettings;
    private SettingsDto warmUpSettings;
    private List<TestCaseDto> testCases;
}
