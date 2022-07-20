package ashes.of.bomber.carrier.services;

import ashes.of.bomber.carrier.dto.events.SinkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.net.URI;

@Service
public class CarrierService {
    private static final Logger log = LogManager.getLogger();

    private final WebClient webClient = WebClient.builder()
            .build();

    private final ReactiveDiscoveryClient discoveryClient;
    private final Registration registration;


    public CarrierService(ReactiveDiscoveryClient discoveryClient, Registration instance) {
        this.discoveryClient = discoveryClient;
        this.registration = instance;
    }

    public Registration getRegistration() {
        return registration;
    }

    public Flux<ServiceInstance> getAtc() {
        return discoveryClient.getInstances("bomber-atc");
    }


    public Flux<ResponseEntity<Void>> event(SinkEvent event) {
        return getAtc()
                .flatMap(atc -> {
                    URI uri = atc.getUri();
                    return webClient.post()
                            .uri(uri + "/atc/sink")
                            .body(BodyInserters.fromValue(event))
                            .retrieve()
                            .toBodilessEntity()
                            .onErrorContinue((throwable, o) -> log.warn("Can't send status to ATC instance: {}, uri: {}", atc.getInstanceId(), atc.getUri(), throwable));
                });
    }
}
