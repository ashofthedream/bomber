package ashes.of.bomber.carrier.starter.sink;

import ashes.of.bomber.carrier.dto.events.SinkEvent;
import ashes.of.bomber.carrier.dto.events.SinkEventType;
import ashes.of.bomber.carrier.starter.services.AtcService;
import ashes.of.bomber.descriptions.TestAppDescription;
import ashes.of.bomber.flight.FlightPlan;
import ashes.of.bomber.flight.Settings;
import ashes.of.bomber.flight.Stage;
import ashes.of.bomber.runner.TestApp;
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
import java.util.Optional;

import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_APP_FINISH;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_APP_START;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_CASE_FINISH;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_CASE_START;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_SUITE_FINISH;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_SUITE_START;

@Component
public class CarrierSink implements Sink {
    private static final Logger log = LogManager.getLogger();

    private final WebClient webClient = WebClient.builder()
            .build();

    private final AtcService atcService;
    private final ServiceInstanceRegistration registration;
    private final TestApp app;

    public CarrierSink(AtcService atcService, ServiceInstanceRegistration registration, TestApp app) {
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
        return new SinkEvent()
                .setType(type)
                .setTimestamp(timestamp.toEpochMilli())
                .setFlightId(Optional.ofNullable(app.getFlightPlan()).map(FlightPlan::getId).orElse(0L))
                .setCarrierId(registration.getServiceInstance().getId())
                .setStage(stage != null ? stage.name() : null)
                .setTestSuite(testSuite)
                .setTestCase(testCase);

    }
}
