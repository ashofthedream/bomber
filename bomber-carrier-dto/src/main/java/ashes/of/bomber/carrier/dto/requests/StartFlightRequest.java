package ashes.of.bomber.carrier.dto.requests;

import ashes.of.bomber.carrier.dto.FlightPlanDto;

public class StartFlightRequest {
    private FlightPlanDto plan;

    public FlightPlanDto getPlan() {
        return plan;
    }

    public StartFlightRequest setPlan(FlightPlanDto plan) {
        this.plan = plan;
        return this;
    }
}
