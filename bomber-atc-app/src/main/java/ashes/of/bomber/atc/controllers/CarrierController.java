package ashes.of.bomber.atc.controllers;

import ashes.of.bomber.atc.dto.ResponseEntities;
import ashes.of.bomber.atc.services.CarrierService;
import ashes.of.bomber.carrier.dto.carrier.CarrierDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping
public class CarrierController {
    private static final Logger log = LogManager.getLogger();

    private final CarrierService carrierService;

    public CarrierController(CarrierService carrierService) {
        this.carrierService = carrierService;
    }


    /**
     * @return all active carriers with all application
     */
    @GetMapping("/atc/carriers/active")
    public Mono<List<CarrierDto>> getActive() {
        log.debug("get active carriers");

        return carrierService.getCarriers()
                .flatMap(carrierService::status)
                .collectList();
    }


    @PostMapping("/atc/carriers/applications/stop")
    public ResponseEntity<?> stopAllApplications() {
        log.debug("stop all applications on all active carriers");

        carrierService.getCarriers()
                .subscribe(carrierService::stop);

        return ResponseEntities.ok();
    }
}
