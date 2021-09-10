package ashes.of.bomber.carrier.dto.requests;

import java.util.List;

public class FlightsStartedResponse {
    private List<FlightStartedResponse> flights;

    public List<FlightStartedResponse> getFlights() {
        return flights;
    }

    public FlightsStartedResponse setFlights(List<FlightStartedResponse> flights) {
        this.flights = flights;
        return this;
    }
}
