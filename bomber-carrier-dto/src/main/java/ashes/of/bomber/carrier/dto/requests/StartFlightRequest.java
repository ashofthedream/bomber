package ashes.of.bomber.carrier.dto.requests;

import ashes.of.bomber.carrier.dto.flight.TestFlightDto;

public class StartFlightRequest {
    private TestFlightDto flight;

    public TestFlightDto getFlight() {
        return flight;
    }

    public StartFlightRequest setFlight(TestFlightDto flight) {
        this.flight = flight;
        return this;
    }

    @Override
    public String toString() {
        return "StartFlightRequest{" +
                "flight=" + flight +
                '}';
    }
}
