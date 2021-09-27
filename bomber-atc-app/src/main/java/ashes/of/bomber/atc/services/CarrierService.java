package ashes.of.bomber.atc.services;

import ashes.of.bomber.atc.models.Carrier;
import ashes.of.bomber.atc.models.Flight;
import ashes.of.bomber.carrier.dto.carrier.CarrierDto;
import ashes.of.bomber.carrier.dto.flight.TestAppDto;
import ashes.of.bomber.carrier.dto.requests.FlightStartedResponse;
import ashes.of.bomber.carrier.dto.requests.GetApplicationsResponse;
import ashes.of.bomber.carrier.dto.requests.StartFlightRequest;
import ashes.of.bomber.carrier.mappers.TestFlightMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;


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
                .uri(uri + "/carrier/applications")
                .retrieve()
                .bodyToMono(GetApplicationsResponse.class)
                .map(response -> toCarrier(carrier, response.getTestApps()))
                .onErrorContinue((throwable, o) -> log.warn("Can't get status of carrier: {}", carrier.getId(), throwable));
    }

    public Mono<FlightStartedResponse> start(Carrier carrier, Flight flight) {
        URI uri = carrier.getInstance().getUri();
        StartFlightRequest request = new StartFlightRequest()
                .setFlight(TestFlightMapper.toDto(flight.getPlan()));

        return webClient.post()
                .uri(uri + "/carrier/applications/start")
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(FlightStartedResponse.class)
                .onErrorContinue((throwable, o) -> log.warn("Can't start flight on carrier: {}", carrier.getId(), throwable));
    }


    public void stop(Carrier carrier) {
        URI uri = carrier.getInstance().getUri();
        webClient.post()
                .uri(uri + "/carrier/applications/stop")
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        response -> log.debug("application on carrier: {} started", carrier.getId()),
                        throwable -> log.warn("application on carrier: {} hasn't started", carrier.getId(), throwable));
    }


    private CarrierDto toCarrier(Carrier carrier, List<TestAppDto> apps) {
        return new CarrierDto()
                .setId(carrier.getId())
                .setApps(apps)
                .setUri(carrier.getInstance().getUri());
    }
}
