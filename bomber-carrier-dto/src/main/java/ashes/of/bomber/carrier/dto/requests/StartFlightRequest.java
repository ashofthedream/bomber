package ashes.of.bomber.carrier.dto.requests;

import ashes.of.bomber.carrier.dto.flight.TestFlightDto;

public class StartFlightRequest {
    private TestFlightDto plan;

    public TestFlightDto getPlan() {
        return plan;
    }

    public StartFlightRequest setPlan(TestFlightDto plan) {
        this.plan = plan;
        return this;
    }
}
