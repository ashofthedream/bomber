package ashes.of.bomber.atc.controllers;

import ashes.of.bomber.atc.dto.TestFlightsDto;
import ashes.of.bomber.atc.services.CarrierService;
import ashes.of.bomber.atc.dto.CarrierDto;
import ashes.of.bomber.atc.dto.ResponseEntities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping("/atc/carriers")
public class CarrierController {
    private static final Logger log = LogManager.getLogger();

    private final ReactiveDiscoveryClient discoveryClient;
    private final CarrierService carrierService;

    public CarrierController(ReactiveDiscoveryClient discoveryClient, CarrierService carrierService) {
        this.discoveryClient = discoveryClient;
        this.carrierService = carrierService;
    }


    /**
     * @return all active carrier instances with all application
     */
    @GetMapping("/active")
    public Mono<List<CarrierDto>> getActiveCarriers() {
        log.debug("get active carriers");

        return discoveryClient.getInstances("bomber-carrier")
                .flatMap(carrierService::status)
                .collectList();
    }


    @PostMapping("/applications/start")
    public Mono<TestFlightsDto> startAll() {
        log.debug("start all applications on all active carriers");

        return discoveryClient.getInstances("bomber-carrier")
                .flatMap(carrierService::start)
                .collectList()
                .map(flights -> TestFlightsDto.builder()
                        .flights(flights)
                        .build());
    }

    @PostMapping("/{carrierId}/applications/{appId}/start")
    public Mono<TestFlightsDto> startApplicationOnCarrierById(@PathVariable("carrierId") String carrierId, @PathVariable("appId") String appId) {
        log.debug("start application: {} on carrier: {}", appId, carrierId);

        return discoveryClient.getInstances("bomber-carrier")
                .filter(instance -> instance.getInstanceId().equals(carrierId))
                .flatMap(carrierService::start)
                .collectList()
                .map(flights -> TestFlightsDto.builder()
                        .flights(flights)
                        .build());
    }


    @PostMapping("/applications/stop")
    public ResponseEntity<?> stopAllApplications() {
        log.debug("stop all applications on all active carriers");

        discoveryClient.getInstances("bomber-carrier")
                .subscribe(carrierService::stop);

        return ResponseEntities.ok();
    }

    @PostMapping("/{carrierId}/applications/{appId}/stop")
    public ResponseEntity<?> stopApplicationOnCarrierById(@PathVariable("carrierId") String carrierId, @PathVariable("appId") String appId) {
        log.debug("stop application: {} on carrier: {}", appId, carrierId);

        discoveryClient.getInstances("bomber-carrier")
                .filter(instance -> instance.getInstanceId().equals(carrierId))
                .subscribe(carrierService::stop);

        return ResponseEntities.ok();
    }
}
