package ashes.of.bomber.atc.dto.flights;

import ashes.of.bomber.carrier.dto.ApplicationStateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlightRecordDto {
    private long timestamp;
    private String type;
    private String testSuite;
    private String testCase;
    private ApplicationStateDto state;
}
