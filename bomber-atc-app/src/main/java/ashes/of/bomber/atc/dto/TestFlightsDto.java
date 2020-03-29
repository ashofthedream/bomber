package ashes.of.bomber.atc.dto;

import ashes.of.bomber.carrier.dto.TestFlightDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestFlightsDto {
    List<TestFlightDto> flights;
}
