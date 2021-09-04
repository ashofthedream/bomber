package ashes.of.bomber.carrier.dto.requests;

public class StartFlightRequest {
    private long flightId;

    public long getFlightId() {
        return flightId;
    }

    public StartFlightRequest setFlightId(long flightId) {
        this.flightId = flightId;
        return this;
    }
}
