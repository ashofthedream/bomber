package ashes.of.bomber.atc.dto.flights;

import java.util.Map;

public class FlightDto {
    private long id;
    private Map<String, FlightDataDto> data;

    public long getId() {
        return id;
    }

    public FlightDto setId(long id) {
        this.id = id;
        return this;
    }

    public Map<String, FlightDataDto> getData() {
        return data;
    }

    public FlightDto setData(Map<String, FlightDataDto> data) {
        this.data = data;
        return this;
    }
}
