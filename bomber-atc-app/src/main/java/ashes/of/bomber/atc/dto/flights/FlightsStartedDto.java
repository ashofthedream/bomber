package ashes.of.bomber.atc.dto.flights;

import ashes.of.bomber.carrier.dto.FlightStartedDto;

import java.util.List;

public class FlightsStartedDto {
    private List<FlightStartedDto> flights;

    public List<FlightStartedDto> getFlights() {
        return flights;
    }

    public FlightsStartedDto setFlights(List<FlightStartedDto> flights) {
        this.flights = flights;
        return this;
    }
}
