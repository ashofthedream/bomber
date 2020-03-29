package ashes.of.bomber.atc.dto;

import ashes.of.bomber.carrier.dto.ApplicationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarrierDto {
    private String id;
    private URI uri;
    private ApplicationDto app;
}
