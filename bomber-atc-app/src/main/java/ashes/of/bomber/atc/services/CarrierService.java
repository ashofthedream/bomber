package ashes.of.bomber.atc.services;

import ashes.of.bomber.atc.dto.CarrierDto;
import ashes.of.bomber.atc.mappers.FlightPlanMapper;
import ashes.of.bomber.atc.model.Carrier;
import ashes.of.bomber.atc.model.Flight;
import ashes.of.bomber.carrier.dto.ApplicationDto;
import ashes.of.bomber.carrier.dto.FlightStartedDto;
import ashes.of.bomber.carrier.dto.requests.StartFlightRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;


@Service
public class CarrierService {
    private static final Logger log = LogManager.getLogger();

    private final WebClient webClient = WebClient.builder()
            .build();

    private final ReactiveDiscoveryClient discoveryClient;

    public CarrierService(ReactiveDiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public Flux<Carrier> getCarriers() {
        return discoveryClient.getInstances("bomber-carrier")
                .map(Carrier::new);
    }

    public Mono<Carrier> getCarrier(String carrierId) {
        return getCarriers()
                .filter(carrier -> carrier.getId().equals(carrierId))
                .single();
    }

    public Mono<CarrierDto> status(Carrier carrier) {
        URI uri = carrier.getInstance().getUri();
        return webClient.get()
                .uri(uri + "/applications")
                .retrieve()
                .bodyToMono(ApplicationDto.class)
                .map(app -> toCarrier(carrier, app))
                .onErrorContinue((throwable, o) -> log.warn("Can't get status of carrier: {}", carrier.getId(), throwable));
    }

    public Mono<FlightStartedDto> start(Carrier carrier, Flight flight) {
        URI uri = carrier.getInstance().getUri();
        StartFlightRequest request = new StartFlightRequest()
                .setPlan(FlightPlanMapper.toDto(flight.getPlan()));

        return webClient.post()
                .uri(uri + "/applications/start")
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(FlightStartedDto.class)
                .onErrorContinue((throwable, o) -> log.warn("Can't start flight on carrier: {}", carrier.getId(), throwable));
    }


    public void stop(Carrier carrier) {
        URI uri = carrier.getInstance().getUri();
        webClient.post()
                .uri(uri + "/applications/stop")
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        response -> log.debug("application on carrier: {} started", carrier.getId()),
                        throwable -> log.warn("application on carrier: {} hasn't started", carrier.getId(), throwable));
    }



    private CarrierDto toCarrier(Carrier carrier, ApplicationDto app) {
        return new CarrierDto()
                .setApp(app)
                .setId(carrier.getId())
                .setUri(carrier.getInstance().getUri());
    }
}
