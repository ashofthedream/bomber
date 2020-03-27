package ashes.of.bomber.atc.controllers;

import ashes.of.bomber.atc.services.DispatcherService;
import ashes.of.bomber.atc.dto.AppInstanceDto;
import ashes.of.bomber.atc.dto.ResponseEntities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping("/dispatchers")
public class DispatcherController {
    private static final Logger log = LogManager.getLogger();

    private final ReactiveDiscoveryClient discoveryClient;
    private final DispatcherService dispatcherService;

    public DispatcherController(ReactiveDiscoveryClient discoveryClient, DispatcherService dispatcherService) {
        this.discoveryClient = discoveryClient;
        this.dispatcherService = dispatcherService;
    }


    /**
     * @return all managed instances
     */
    @GetMapping("/active")
    public Mono<List<AppInstanceDto>> getActiveDispatchers() {
        log.debug("get active dispatchers");

        return discoveryClient.getInstances("bomber-dispatcher")
                .flatMap(dispatcherService::status)
                .collectList();
    }

    @PostMapping("/applications/start/all")
    public ResponseEntity<?> startAll() {
        log.debug("start all active dispatchers");

        discoveryClient.getInstances("bomber-dispatcher")
                .subscribe(dispatcherService::start);

        return ResponseEntities.ok();
    }

    @PostMapping("/applications/start/{id}")
    public ResponseEntity<?> startById(@PathVariable("id") String id) {
        log.debug("start all active dispatchers");

        discoveryClient.getInstances("bomber-dispatcher")
                .filter(instance -> instance.getServiceId().equals(id))
                .subscribe(dispatcherService::start);

        return ResponseEntities.ok();
    }


    @PostMapping("/shutdown/all")
    public ResponseEntity<?> shutdownAll() {
        log.debug("shutdown all active dispatchers");

        discoveryClient.getInstances("bomber-dispatcher")
                .subscribe(dispatcherService::shutdown);

        return ResponseEntities.ok();
    }

    @PostMapping("/applications/shutdown/{id}")
    public ResponseEntity<?> shutdownById(@PathVariable("id") String id) {
        log.debug("shutdown all active dispatchers");

        discoveryClient.getInstances("bomber-dispatcher")
                .filter(instance -> instance.getServiceId().equals(id))
                .subscribe(dispatcherService::shutdown);

        return ResponseEntities.ok();
    }


}
