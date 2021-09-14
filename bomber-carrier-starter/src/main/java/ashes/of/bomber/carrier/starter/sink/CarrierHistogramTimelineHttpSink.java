package ashes.of.bomber.carrier.starter.sink;

import ashes.of.bomber.carrier.dto.events.HistogramPointDto;
import ashes.of.bomber.carrier.dto.events.SinkEvent;
import ashes.of.bomber.carrier.starter.services.CarrierService;
import ashes.of.bomber.events.TestAppFinishedEvent;
import ashes.of.bomber.events.TestCaseFinishedEvent;
import ashes.of.bomber.flight.TestFlightPlan;
import ashes.of.bomber.runner.TestApp;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.sink.histogram.HistogramTimelineDummyPrinter;
import ashes.of.bomber.sink.histogram.HistogramTimelineSink;
import ashes.of.bomber.sink.histogram.MeasurementKey;
import ashes.of.bomber.tools.Record;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.zookeeper.serviceregistry.ServiceInstanceRegistration;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static ashes.of.bomber.carrier.dto.events.SinkEventType.TEST_CASE_HISTOGRAM;

@Component
public class CarrierHistogramTimelineHttpSink implements Sink {
    private static final Logger log = LogManager.getLogger();

    private final AtomicLong lastUpdate = new AtomicLong(0);
    private final HistogramTimelineSink sink;

    private final CarrierService carrierService;
    private final ServiceInstanceRegistration registration;

    public CarrierHistogramTimelineHttpSink(CarrierService carrierService, ServiceInstanceRegistration registration) {
        this.carrierService = carrierService;
        this.registration = registration;
        this.sink = new HistogramTimelineSink(ChronoUnit.SECONDS, new HistogramTimelineDummyPrinter());
    }

    @Override
    public void timeRecorded(Record record) {
        sink.timeRecorded(record);
        var current = System.currentTimeMillis() / 1000;
        var last = lastUpdate.get();
        if (last != current && lastUpdate.compareAndSet(last, current)) {
            var it = record.getIteration();
            var key = new MeasurementKey(it.getTestApp(), it.getTestSuite(), it.getTestCase(), it.getStage());
            sendHistogramByKey(record.getIteration().getFlightId(), key);
        }
    }

    private void sendHistogramByKey(long fligtId, MeasurementKey key) {
        var timeline = sink.getTimeline(key);

        var histograms = timeline.entrySet().stream()
                .flatMap(entry -> {
                    var time = entry.getKey();
                    var measurement = entry.getValue();

                    return measurement.getHistograms().entrySet()
                            .stream()
                            .map(hae -> {

                                var h = hae.getValue().getHistogram();

                                return new HistogramPointDto()
                                        .setLabel(hae.getKey())
                                        .setTimestamp(time.toEpochMilli())
                                        .setTotalCount(h.getTotalCount())
                                        .setErrorsCount(hae.getValue().getErrorsCount())
                                        .setPercentiles(
                                                ms(h.getMinNonZeroValue()),
                                                ms(h.getValueAtPercentile(0.5)),
                                                ms(h.getValueAtPercentile(0.75)),
                                                ms(h.getValueAtPercentile(0.90)),
                                                ms(h.getValueAtPercentile(0.95)),
                                                ms(h.getValueAtPercentile(0.99)),
                                                ms(h.getValueAtPercentile(0.999)),
                                                ms(h.getMaxValue())
                                        );
                            });
                })
                .collect(Collectors.toList());

        send(new SinkEvent()
                .setId(SinkEvent.nextId())
                .setType(TEST_CASE_HISTOGRAM)
                .setTimestamp(System.currentTimeMillis())
                .setFlightId(fligtId)
                .setCarrierId(registration.getServiceInstance().getId())
                .setStage(key.getStage())
                .setTestApp(key.getTestApp())
                .setTestSuite(key.getTestSuite())
                .setTestCase(key.getTestCase())
                .setHistograms(histograms)
        );
    }

    private static double ms(long ns) {
        return ns / 1_000_000.0;
    }

    @Override
    public void afterTestCase(TestCaseFinishedEvent event) {
        sink.afterTestCase(event);
        var key = new MeasurementKey(event.getTestApp(), event.getTestSuite(), event.getTestCase(), event.getStage());
        sendHistogramByKey(event.getFlightId(), key);
    }

    @Override
    public void afterTestApp(TestAppFinishedEvent event) {
        sink.afterTestApp(event);
    }

    private void send(SinkEvent event) {
        carrierService.event(event).subscribe();
    }
}
