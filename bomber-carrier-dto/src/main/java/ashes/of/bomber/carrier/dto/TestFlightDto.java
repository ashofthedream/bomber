package ashes.of.bomber.carrier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestFlightDto {
    private long id;
    private long startedAt;
    private ApplicationDto app;
}
