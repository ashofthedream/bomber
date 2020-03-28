package ashes.of.bomber.atc.services;

import ashes.of.bomber.atc.dto.AppInstanceDto;
import ashes.of.bomber.dispatcher.dto.ApplicationDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class DispatcherService {
    private static final Logger log = LogManager.getLogger();

    private final WebClient webClient = WebClient.builder()
            .build();


    public Mono<AppInstanceDto> status(ServiceInstance instance) {
        URI uri = instance.getUri();
        return webClient.get()
                .uri(uri + "/application")
                .retrieve()
                .bodyToMono(ApplicationDto.class)
                .map(app -> AppInstanceDto.builder()
                        .app(app)
                        .id(instance.getInstanceId())
                        .uri(uri)
                        .build())
                .onErrorContinue((throwable, o) ->
                        log.warn("Can't get state of dispatcher: {}", instance.getInstanceId(), throwable));
    }

    public void start(ServiceInstance instance) {
        URI uri = instance.getUri();
        webClient.post()
                .uri(uri + "/application/run")
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        response -> log.debug("dispatcher: {} started", uri),
                        throwable -> log.warn("dispatcher: {} hasn't started", uri, throwable));
    }


    public void shutdown(ServiceInstance instance) {
        URI uri = instance.getUri();
        webClient.post()
                .uri(uri + "/application/shutdown")
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        response -> log.debug("dispatcher: {} started", uri),
                        throwable -> log.warn("dispatcher: {} hasn't started", uri, throwable));
    }
}
