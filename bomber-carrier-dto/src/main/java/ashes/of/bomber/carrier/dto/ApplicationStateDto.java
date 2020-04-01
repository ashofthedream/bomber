package ashes.of.bomber.carrier.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStateDto {
    private String stage;
    private SettingsDto settings;
    private String testSuite;
    private String testCase;
    private long testSuiteStart;
    private long testCaseStart;
    private long remainTotalIterations;
    private long elapsedTime;
    private long remainTime;
    private long errorsCount;
    private List<WorkerStateDto> workers;
}
