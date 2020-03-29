package ashes.of.bomber.atc.services;

import ashes.of.bomber.atc.dto.CarrierDto;
import ashes.of.bomber.carrier.dto.ApplicationDto;
import ashes.of.bomber.carrier.dto.TestFlightDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;

@Service
public class CarrierService {
    private static final Logger log = LogManager.getLogger();

    private final WebClient webClient = WebClient.builder()
            .build();


    public Mono<CarrierDto> status(ServiceInstance instance) {
        URI uri = instance.getUri();
        return webClient.get()
                .uri(uri + "/applications")
                .retrieve()
                .bodyToMono(ApplicationDto.class)
                .map(app -> CarrierDto.builder()
                        .app(app)
                        .id(instance.getInstanceId())
                        .uri(uri)
                        .build())
                .onErrorContinue((throwable, o) ->
                        log.warn("Can't get state of carrier: {}", instance.getInstanceId(), throwable));
    }

    public Mono<TestFlightDto> start(ServiceInstance instance) {
        URI uri = instance.getUri();
        return webClient.post()
                .uri(uri + "/applications/start")
                .retrieve()
                .bodyToMono(TestFlightDto.class)
                .onErrorContinue((throwable, o) -> log.warn("Can't start app on carrier: {}", instance.getInstanceId(), throwable));
    }


    public void stop(ServiceInstance instance) {
        URI uri = instance.getUri();
        webClient.post()
                .uri(uri + "/applications/stop")
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        response -> log.debug("application on carrier: {} started", uri),
                        throwable -> log.warn("application on carrier: {} hasn't started", uri, throwable));
    }
}
