package ashes.of.bomber.carrier.starter.sink;

import ashes.of.bomber.carrier.dto.ApplicationStateDto;
import ashes.of.bomber.carrier.dto.events.SinkEvent;
import ashes.of.bomber.carrier.dto.events.SinkEventType;
import ashes.of.bomber.carrier.starter.mappers.ApplicationMapper;
import ashes.of.bomber.carrier.starter.services.CarrierService;
import ashes.of.bomber.flight.FlightPlan;
import ashes.of.bomber.flight.Iteration;
import ashes.of.bomber.flight.Settings;
import ashes.of.bomber.flight.Stage;
import ashes.of.bomber.runner.TestApp;
import ashes.of.bomber.sink.Sink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.zookeeper.serviceregistry.ServiceInstanceRegistration;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_APP_FINISH;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_APP_START;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_CASE_FINISH;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_CASE_PROGRESS;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_CASE_START;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_SUITE_FINISH;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_SUITE_START;

@Component
public class CarrierHttpSink implements Sink {
    private static final Logger log = LogManager.getLogger();

    private final WebClient webClient = WebClient.builder()
            .build();

    private final CarrierService carrierService;
    private final ServiceInstanceRegistration registration;
    private final TestApp app;

    private final AtomicLong lastUpdate = new AtomicLong(0);

    public CarrierHttpSink(CarrierService carrierService, ServiceInstanceRegistration registration, TestApp app) {
        this.carrierService = carrierService;
        this.registration = registration;
        this.app = app;
    }

    @Override
    public void startUp(Instant timestamp) {
        SinkEvent event = event(TEST_APP_START, timestamp, null,null, null, null);
        send(event);
    }

    @Override
    public void beforeTestSuite(Instant timestamp, String testSuite) {
        SinkEvent event = event(TEST_SUITE_START, timestamp, null, testSuite, null, null);
        send(event);
    }

    @Override
    public void afterEach(Iteration it, long elapsed, @Nullable Throwable throwable) {
        var current = System.currentTimeMillis() / 1000;
        var last = lastUpdate.get();
        if (last != current && lastUpdate.compareAndSet(last, current)) {
            var state = ApplicationMapper.toDto(app.getState());
            SinkEvent event = event(TEST_CASE_PROGRESS, Instant.now(), it.getStage(), it.getTestSuite(), it.getTestCase(), state);
            send(event);
        }
    }

    @Override
    public void beforeTestCase(Instant timestamp, Stage stage, String testSuite, String testCase, Settings settings) {
        SinkEvent event = event(TEST_CASE_START, timestamp, stage, testSuite, testCase, null);
        send(event);
    }

    @Override
    public void afterTestCase(Stage stage, String testSuite, String testCase) {
        SinkEvent event = event(TEST_CASE_FINISH, Instant.now(), stage, testSuite, testCase, null);
        send(event);
    }

    @Override
    public void afterTestSuite(String testSuite) {
        SinkEvent event = event(TEST_SUITE_FINISH, Instant.now(), null, testSuite,null, null);
        send(event);
    }

    @Override
    public void shutDown(Instant timestamp) {
        SinkEvent event = event(TEST_APP_FINISH, timestamp, null, null, null, null);
        send(event);
    }

    private void send(SinkEvent event) {
        carrierService.event(event).subscribe();
    }

    private SinkEvent event(SinkEventType type, Instant timestamp, @Nullable Stage stage, @Nullable String testSuite, @Nullable String testCase, @Nullable ApplicationStateDto state) {
        return new SinkEvent()
                .setId(SinkEvent.nextId())
                .setType(type)
                .setTimestamp(timestamp.toEpochMilli())
                .setFlightId(Optional.ofNullable(app.getFlightPlan()).map(FlightPlan::getFlightId).orElse(0L))
                .setCarrierId(registration.getServiceInstance().getId())
                .setStage(stage != null ? stage.name() : null)
                .setTestSuite(testSuite)
                .setTestCase(testCase)
                .setState(state);
    }
}
