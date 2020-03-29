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
public class ApplicationDto {
    private String name;
    private List<TestSuiteDto> testSuites;
}
