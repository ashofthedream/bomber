package ashes.of.bomber.carrier.mappers;

import ashes.of.bomber.carrier.dto.flight.TestFlightDto;
import ashes.of.bomber.flight.TestFlightPlan;

import java.util.stream.Collectors;

public class TestFlightMapper {

    public static TestFlightPlan toPlan(TestFlightDto flight) {
        var testApps = flight.getTestApps()
                .stream()
                .map(TestAppMapper::toPlan)
                .collect(Collectors.toList());

        return new TestFlightPlan(flight.getId(), testApps);
    }

    public static TestFlightDto toDto(TestFlightPlan flight) {
        var testApps = flight.getTestApps()
                .stream()
                .map(TestAppMapper::toDto)
                .collect(Collectors.toList());

        return new TestFlightDto()
                .setId(flight.getFlightId())
                .setTestApps(testApps);
    }
}
