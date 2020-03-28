package ashes.of.bomber.atc.dto;

import ashes.of.bomber.dispatcher.dto.ApplicationDto;
import lombok.Builder;
import lombok.Data;

import java.net.URI;

@Data
@Builder
public class AppInstanceDto {
    private String id;
    private URI uri;
    private ApplicationDto app;
}
