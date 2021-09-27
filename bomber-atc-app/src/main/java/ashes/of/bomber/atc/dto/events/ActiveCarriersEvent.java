package ashes.of.bomber.atc.dto.events;

import ashes.of.bomber.carrier.dto.carrier.CarrierDto;

import java.util.List;

public class ActiveCarriersEvent {
    private final EventType type = EventType.ACTIVE_CARRIERS;
    private List<CarrierDto> carriers;

    public EventType getType() {
        return type;
    }

    public List<CarrierDto> getCarriers() {
        return carriers;
    }

    public ActiveCarriersEvent setCarriers(List<CarrierDto> carriers) {
        this.carriers = carriers;
        return this;
    }
}
