package ashes.of.bomber.carrier.starter.controllers;

import ashes.of.bomber.carrier.dto.carrier.CarrierDto;
import ashes.of.bomber.carrier.starter.services.CarrierService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping
public class CarrierController {
    private static final Logger log = LogManager.getLogger();

    private final CarrierService carrierService;

    public CarrierController(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    @GetMapping("/carrier")
    public Mono<CarrierDto> getCarrier() {
        log.debug("get carrier");
        var registration = carrierService.getRegistration();

        var dto = new CarrierDto()
                .setId(registration.getInstanceId());

        return Mono.just(dto);
    }
}
