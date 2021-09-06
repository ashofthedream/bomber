package ashes.of.bomber.atc.dto.flights;


import java.util.List;

public class FlightProgressDto {
    private String carrierId;
    private List<FlightRecordDto> records;
    private FlightRecordDto actual;

    public String getCarrierId() {
        return carrierId;
    }

    public FlightProgressDto setCarrierId(String carrierId) {
        this.carrierId = carrierId;
        return this;
    }

    public List<FlightRecordDto> getRecords() {
        return records;
    }

    public FlightProgressDto setRecords(List<FlightRecordDto> records) {
        this.records = records;
        return this;
    }

    public FlightRecordDto getActual() {
        return actual;
    }

    public FlightProgressDto setActual(FlightRecordDto actual) {
        this.actual = actual;
        return this;
    }
}
