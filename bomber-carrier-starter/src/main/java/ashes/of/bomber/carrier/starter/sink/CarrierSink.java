package ashes.of.bomber.carrier.starter.sink;

import ashes.of.bomber.carrier.dto.events.SinkEvent;
import ashes.of.bomber.carrier.dto.events.SinkEventType;
import ashes.of.bomber.carrier.starter.services.AtcService;
import ashes.of.bomber.core.BomberApp;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.Stage;
import ashes.of.bomber.sink.Sink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.zookeeper.serviceregistry.ServiceInstanceRegistration;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Nullable;
import java.net.URI;
import java.time.Instant;

import static ashes.of.bomber.carrier.dto.events.SinkEventType.*;

@Component
public class CarrierSink implements Sink {
    private static final Logger log = LogManager.getLogger();

    private final WebClient webClient = WebClient.builder()
            .build();

    private final AtcService atcService;
    private final ServiceInstanceRegistration registration;
    private final BomberApp app;

    public CarrierSink(AtcService atcService, ServiceInstanceRegistration registration, BomberApp app) {
        this.atcService = atcService;
        this.registration = registration;
        this.app = app;
    }

    @Override
    public void startUp() {
        SinkEvent event = event(TEST_APP_START, Instant.now(), null,null, null);
        send(event);
    }

    @Override
    public void beforeTestSuite(String testSuite, Instant timestamp) {
        SinkEvent event = event(TEST_SUITE_START, timestamp, null, testSuite, null);
        send(event);
    }


    @Override
    public void beforeTestCase(Stage stage, String testSuite, String testCase, Instant timestamp, Settings settings) {
        SinkEvent event = event(TEST_CASE_START, timestamp, stage, testSuite, testCase);
        send(event);
    }

    @Override
    public void afterTestCase(Stage stage, String testSuite, String testCase) {
        SinkEvent event = event(TEST_CASE_FINISH, Instant.now(), stage, testSuite, testCase);
        send(event);
    }

    @Override
    public void afterTestSuite(String testSuite) {
        SinkEvent event = event(TEST_SUITE_FINISH, Instant.now(), null, testSuite,null);
        send(event);
    }

    @Override
    public void shutDown() {
        SinkEvent event = event(TEST_APP_FINISH, Instant.now(), null, null, null);
        send(event);
    }

    private void send(SinkEvent event) {
        atcService.getAtc()
                .flatMap(atc -> {
                    URI uri = atc.getInstance().getUri();
                    return webClient.post()
                            .uri(uri + "/atc/sink")
                            .body(BodyInserters.fromValue(event))
                            .retrieve()
                            .toBodilessEntity()
                            .onErrorContinue((throwable, o) -> log.warn("Can't send status to ATC: {}", atc.getId(), throwable));
                })
                .subscribe();
    }

    private SinkEvent event(SinkEventType type, Instant timestamp, @Nullable Stage stage, @Nullable String testSuite, @Nullable String testCase) {
        return SinkEvent.builder()
                .type(type)
                .timestamp(timestamp.toEpochMilli())
                .flightId(app.getPlan().getId())
                .carrierId(registration.getServiceInstance().getId())
                .stage(stage != null ? stage.name() : null)
                .testSuite(testSuite)
                .testCase(testCase)
                .build();
    }
}
