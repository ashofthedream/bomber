package ashes.of.bomber.atc.dto.flights;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlightDto {
    private long id;
    private Map<String, FlightDataDto> data;
}
