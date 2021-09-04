package ashes.of.bomber.carrier.starter.services;

import ashes.of.bomber.carrier.starter.model.Atc;
import ashes.of.bomber.descriptions.TestAppDescription;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class AtcService {
    private static final Logger log = LogManager.getLogger();

    private final ReactiveDiscoveryClient discoveryClient;
    private final Registration registration;


    public AtcService(ReactiveDiscoveryClient discoveryClient, Registration instance, TestAppDescription app) {
        this.discoveryClient = discoveryClient;
        this.registration = instance;
    }

    public Flux<Atc> getAtc() {
        return discoveryClient.getInstances("bomber-atc")
                .map(Atc::new);
    }
}
