package ashes.of.bomber.carrier.starter.sink;

import ashes.of.bomber.Bomber;
import ashes.of.bomber.carrier.dto.events.SinkEvent;
import ashes.of.bomber.carrier.mappers.TestAppMapper;
import ashes.of.bomber.carrier.starter.services.CarrierService;
import ashes.of.bomber.events.TestAppFinishedEvent;
import ashes.of.bomber.events.TestAppStartedEvent;
import ashes.of.bomber.events.TestCaseAfterEachEvent;
import ashes.of.bomber.events.TestCaseFinishedEvent;
import ashes.of.bomber.events.TestCaseStartedEvent;
import ashes.of.bomber.events.TestSuiteFinishedEvent;
import ashes.of.bomber.events.TestSuiteStartedEvent;
import ashes.of.bomber.sink.Sink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.zookeeper.serviceregistry.ServiceInstanceRegistration;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_APP_FINISH;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_APP_START;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_CASE_FINISH;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_CASE_PROGRESS;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_CASE_START;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_SUITE_FINISH;
import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_SUITE_START;

@Component
public class CarrierFlightProgressHttpSink implements Sink {
    private static final Logger log = LogManager.getLogger();

    private final CarrierService carrierService;
    private final ServiceInstanceRegistration registration;
    private final Bomber bomber;

    private final AtomicLong lastUpdate = new AtomicLong(0);

    public CarrierFlightProgressHttpSink(CarrierService carrierService, ServiceInstanceRegistration registration, Bomber bomber) {
        this.carrierService = carrierService;
        this.registration = registration;
        this.bomber = bomber;
    }

    @Override
    public void beforeTestApp(TestAppStartedEvent event) {
        send(new SinkEvent()
                .setId(SinkEvent.nextId())
                .setType(TEST_APP_START)
                .setCarrierId(registration.getServiceInstance().getId())
                .setTimestamp(event.getTimestamp().toEpochMilli())
                .setFlightId(event.getFlightId())
                .setTestApp(event.getTestApp()));
    }

    @Override
    public void beforeTestSuite(TestSuiteStartedEvent event) {
        send(new SinkEvent()
                .setId(SinkEvent.nextId())
                .setType(TEST_SUITE_START)
                .setCarrierId(registration.getServiceInstance().getId())
                .setTimestamp(event.getTimestamp().toEpochMilli())
                .setFlightId(event.getFlightId())
                .setTestApp(event.getTestApp())
                .setTestSuite(event.getTestSuite()));
    }

    @Override
    public void afterEach(TestCaseAfterEachEvent event) {
        var current = System.currentTimeMillis() / 1000;
        var last = lastUpdate.get();
        if (last != current && lastUpdate.compareAndSet(last, current)) {
            var testApp = bomber.getApps().stream()
                    .filter(app -> Objects.equals(app.getName(), event.getTestApp()))
                    .findFirst()
                    .orElseThrow();
            var state = TestAppMapper.toDto(bomber.getState());
            send(new SinkEvent()
                    .setId(SinkEvent.nextId())
                    .setType(TEST_CASE_PROGRESS)
                    .setTimestamp(Instant.now().toEpochMilli())
                    .setCarrierId(registration.getServiceInstance().getId())
                    .setFlightId(event.getFlightId())
                    .setTestApp(event.getTestApp())
                    .setTestSuite(event.getTestSuite())
                    .setTestCase(event.getTestCase())
                    .setStage(event.getStage())
                    .setState(state));
        }
    }

    @Override
    public void beforeTestCase(TestCaseStartedEvent event) {
        send(new SinkEvent()
                .setId(SinkEvent.nextId())
                .setType(TEST_CASE_START)
                .setCarrierId(registration.getServiceInstance().getId())
                .setTimestamp(event.getTimestamp().toEpochMilli())
                .setFlightId(event.getFlightId())
                .setTestApp(event.getTestApp())
                .setTestSuite(event.getTestSuite())
                .setTestCase(event.getTestCase())
                .setStage(event.getStage()));
    }

    @Override
    public void afterTestCase(TestCaseFinishedEvent event) {
        send(new SinkEvent()
                .setId(SinkEvent.nextId())
                .setType(TEST_CASE_FINISH)
                .setCarrierId(registration.getServiceInstance().getId())
                .setTimestamp(event.getTimestamp().toEpochMilli())
                .setFlightId(event.getFlightId())
                .setTestApp(event.getTestApp())
                .setTestSuite(event.getTestSuite())
                .setTestCase(event.getTestCase())
                .setStage(event.getStage())
                .setState(null));
    }

    @Override
    public void afterTestSuite(TestSuiteFinishedEvent event) {
        send(new SinkEvent()
                .setId(SinkEvent.nextId())
                .setType(TEST_SUITE_FINISH)
                .setCarrierId(registration.getServiceInstance().getId())
                .setTimestamp(event.getTimestamp().toEpochMilli())
                .setFlightId(event.getFlightId())
                .setTestApp(event.getTestApp())
                .setTestSuite(event.getTestSuite())
        );
    }

    @Override
    public void afterTestApp(TestAppFinishedEvent event) {
        send(new SinkEvent()
                .setId(SinkEvent.nextId())
                .setType(TEST_APP_FINISH)
                .setCarrierId(registration.getServiceInstance().getId())
                .setTimestamp(event.getTimestamp().toEpochMilli())
                .setFlightId(event.getFlightId())
                .setTestApp(event.getTestApp()));
    }

    private void send(SinkEvent event) {
        carrierService.event(event).subscribe();
    }
}