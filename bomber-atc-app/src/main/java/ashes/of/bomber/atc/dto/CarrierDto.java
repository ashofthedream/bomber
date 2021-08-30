package ashes.of.bomber.atc.dto;

import ashes.of.bomber.carrier.dto.ApplicationDto;

import java.net.URI;

public class CarrierDto {
    private String id;
    private URI uri;
    private ApplicationDto app;

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

    public ApplicationDto getApp() {
        return app;
    }

    public CarrierDto setApp(ApplicationDto app) {
        this.app = app;
        return this;
    }
}
