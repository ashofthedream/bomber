package ashes.of.bomber.atc.controllers;

import ashes.of.bomber.atc.services.CarrierService;
import ashes.of.bomber.atc.dto.CarrierDto;
import ashes.of.bomber.atc.dto.ResponseEntities;
import ashes.of.bomber.atc.services.FlightService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping("/atc/carriers")
public class CarrierController {
    private static final Logger log = LogManager.getLogger();

    private final CarrierService carrierService;

    public CarrierController(CarrierService carrierService) {
        this.carrierService = carrierService;
    }


    /**
     * @return all active carriers with all application
     */
    @GetMapping("/active")
    public Mono<List<CarrierDto>> getActive() {
        log.debug("get active carriers");

        return carrierService.getCarriers()
                .flatMap(carrierService::status)
                .collectList();
    }


    @PostMapping("/applications/stop")
    public ResponseEntity<?> stopAllApplications() {
        log.debug("stop all applications on all active carriers");

        carrierService.getCarriers()
                .subscribe(carrierService::stop);

        return ResponseEntities.ok();
    }

    @PostMapping("/{carrierId}/applications/{appId}/stop")
    public ResponseEntity<?> stopApplicationOnCarrierById(@PathVariable("carrierId") String carrierId, @PathVariable("appId") String appId) {
        log.debug("stop application: {} on carrier: {}", appId, carrierId);

        carrierService.getCarrier(carrierId)
                .subscribe(carrierService::stop);

        return ResponseEntities.ok();
    }
}
