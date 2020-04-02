package ashes.of.bomber.atc.dto.flights;

import ashes.of.bomber.carrier.dto.FlightStartedDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlightsStartedDto {
    private List<FlightStartedDto> flights;
}
