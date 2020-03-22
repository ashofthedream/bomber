package ashes.of.bomber.atc.controllers;

import ashes.of.bomber.atc.dto.ResponseEntities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/dispatchers")
public class DispatcherController {
    private static final Logger log = LogManager.getLogger();

    private final WebClient webClient = WebClient.builder()
            .build();

    private final ReactiveDiscoveryClient discoveryClient;

    public DispatcherController(ReactiveDiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }


    /**
     * @return all managed instances
     */
    @GetMapping("/active")
    public Mono<List<ServiceInstance>> getActiveDispatchers() {
        log.debug("get active dispatchers");

        return discoveryClient.getInstances("bomber-dispatcher")
//                .map(instance -> instance.getHost() + ":" + instance.getPort())
                .collectList();
    }

    @PostMapping("/start/all")
    public ResponseEntity<?> startAll() {
        log.debug("start all active dispatchers");

        discoveryClient.getInstances("bomber-dispatcher")
                .subscribe(instance -> {
                    URI uri = instance.getUri();
                    webClient.post()
                            .uri(uri + "/application/run")
                            .retrieve()
                            .toBodilessEntity()
                            .subscribe(
                                    response -> log.debug("dispatcher: {} started", uri),
                                    throwable -> log.warn("dispatcher: {} hasn't started", uri, throwable));
                });

        return ResponseEntities.ok();
    }

    @PostMapping("/shutdown/all")
    public ResponseEntity<?> shutdownAll() {
        log.debug("shutdown all active dispatchers");

        discoveryClient.getInstances("bomber-dispatcher")
                .subscribe(instance -> {
                    URI uri = instance.getUri();
                    webClient.post()
                            .uri(uri + "/application/shutdown")
                            .retrieve()
                            .toBodilessEntity()
                            .subscribe(
                                    response -> log.debug("dispatcher: {} started", uri),
                                    throwable -> log.warn("dispatcher: {} hasn't started", uri, throwable));
                });

        return ResponseEntities.ok();
    }

}
