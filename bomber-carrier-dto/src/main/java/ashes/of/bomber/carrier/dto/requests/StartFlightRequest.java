package ashes.of.bomber.carrier.dto.requests;

import ashes.of.bomber.carrier.dto.flight.TestFlightDto;

import java.util.Set;

public class StartFlightRequest {
    private Set<String> carriers;
    private TestFlightDto flight;

    public Set<String> getCarriers() {
        return carriers;
    }

    public StartFlightRequest setCarriers(Set<String> carriers) {
        this.carriers = carriers;
        return this;
    }

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
