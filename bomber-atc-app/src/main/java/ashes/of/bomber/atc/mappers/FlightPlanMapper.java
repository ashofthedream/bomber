package ashes.of.bomber.atc.mappers;

import ashes.of.bomber.carrier.dto.flight.FlightPlanDto;
import ashes.of.bomber.flight.FlightPlan;

import java.util.stream.Collectors;

public class FlightPlanMapper {

    public static FlightPlan toPlan(long id, FlightPlanDto plan) {
        var testSuites = plan.getTestSuites()
                .stream()
                .map(TestSuitePlanMapper::toPlan)
                .collect(Collectors.toList());

        return new FlightPlan(id, testSuites);
    }


    public static FlightPlanDto toDto(FlightPlan plan) {
        var testSuites = plan.getTestSuites()
                .stream()
                .map(TestSuitePlanMapper::toDto)
                .collect(Collectors.toList());

        return new FlightPlanDto()
                .setId(plan.getFlightId())
                .setTestSuites(testSuites);
    }
}
