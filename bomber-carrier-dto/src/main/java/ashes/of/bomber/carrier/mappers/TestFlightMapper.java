package ashes.of.bomber.carrier.mappers;

import ashes.of.bomber.carrier.dto.flight.TestFlightDto;
import ashes.of.bomber.flight.TestFlightPlan;

import java.util.stream.Collectors;

public class TestFlightMapper {

    public static TestFlightPlan toPlan(TestFlightDto flight) {
        var testSuites = flight.getTestSuites()
                .stream()
                .map(TestSuiteMapper::toPlan)
                .collect(Collectors.toList());

        return new TestFlightPlan(flight.getId(), testSuites);
    }

    public static TestFlightDto toDto(TestFlightPlan flight) {
        var testSuites = flight.getTestSuites()
                .stream()
                .map(TestSuiteMapper::toDto)
                .collect(Collectors.toList());

        return new TestFlightDto()
                .setId(flight.getFlightId())
                .setTestSuites(testSuites);
    }
}
