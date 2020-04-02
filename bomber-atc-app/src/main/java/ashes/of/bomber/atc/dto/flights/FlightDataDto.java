package ashes.of.bomber.atc.dto.flights;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlightDataDto {
    private String carrierId;
    private List<FlightRecordDto> records;
    private FlightRecordDto actual;
}
