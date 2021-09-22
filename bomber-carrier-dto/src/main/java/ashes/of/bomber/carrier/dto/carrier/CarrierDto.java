package ashes.of.bomber.carrier.dto.carrier;

import ashes.of.bomber.carrier.dto.flight.TestAppDto;

import java.net.URI;
import java.util.List;

public class CarrierDto {
    private String id;
    private URI uri;
    private List<TestAppDto> apps;

    public String getId() {
        return id;
    }

    public CarrierDto setId(String id) {
        this.id = id;
        return this;
    }

    public URI getUri() {
        return uri;
    }

    public CarrierDto setUri(URI uri) {
        this.uri = uri;
        return this;
    }

    public List<TestAppDto> getApps() {
        return apps;
    }

    public CarrierDto setApps(List<TestAppDto> apps) {
        this.apps = apps;
        return this;
    }
}
