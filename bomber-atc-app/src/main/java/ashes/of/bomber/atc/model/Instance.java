package ashes.of.bomber.atc.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Instance {

    private final String name;
    private final String stage;

    @JsonCreator
    public Instance(String name, String stage) {
        this.name = name;
        this.stage = stage;
    }
}
