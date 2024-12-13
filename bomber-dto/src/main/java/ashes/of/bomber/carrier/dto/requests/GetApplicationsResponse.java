package ashes.of.bomber.carrier.dto.requests;

import ashes.of.bomber.carrier.dto.flight.TestAppDto;

import java.util.List;

public record GetApplicationsResponse(List<TestAppDto> testApps) {
}
