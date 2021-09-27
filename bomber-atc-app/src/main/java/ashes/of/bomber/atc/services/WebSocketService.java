package ashes.of.bomber.atc.services;

import ashes.of.bomber.atc.dto.events.ActiveCarriersEvent;
import ashes.of.bomber.atc.dto.events.ActiveFlightEvent;
import ashes.of.bomber.atc.dto.events.ActiveFlightHistogramEvent;
import ashes.of.bomber.atc.dto.events.ActiveFlightLogEvent;
import ashes.of.bomber.atc.dto.events.ActiveFlightProgressEvent;
import ashes.of.bomber.atc.dto.events.EventType;
import ashes.of.bomber.atc.models.Flight;
import ashes.of.bomber.atc.models.FlightProgress;
import ashes.of.bomber.carrier.dto.carrier.CarrierDto;
import ashes.of.bomber.carrier.dto.events.HistogramPointDto;
import ashes.of.bomber.carrier.dto.events.SinkEvent;
import ashes.of.bomber.carrier.mappers.TestFlightMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;

@Service
public class WebSocketService {
    private static final Logger log = LogManager.getLogger();

    private final Sinks.Many<Object> sink = Sinks.many()
            .multicast()
            .onBackpressureBuffer(10);

    public Flux<Object> events() {
        return sink.asFlux();
    }

    public void sendCarriers(List<CarrierDto> carriers) {
        sink.tryEmitNext(new ActiveCarriersEvent().setCarriers(carriers));
    }

    public void sendFlightProgress(FlightProgress progress) {
        sink.tryEmitNext(new ActiveFlightProgressEvent().setProgress(progress));
    }

    public void sendFlightHistogram(List<HistogramPointDto> points) {
        sink.tryEmitNext(new ActiveFlightHistogramEvent().setPoints(points));
    }

    public void sendFlightEvent(SinkEvent event) {
        sink.tryEmitNext(new ActiveFlightLogEvent().setEvent(event));
    }

    public void sendFlightStarted(Flight flight) {
        sink.tryEmitNext(new ActiveFlightEvent()
                .setType(EventType.ACTIVE_FLIGHT_STARTED)
                .setPlan(TestFlightMapper.toDto(flight.getPlan())));
    }

    public void sendFlightFinished(Flight flight) {
        sink.tryEmitNext(new ActiveFlightEvent()
                .setType(EventType.ACTIVE_FLIGHT_FINISHED)
                .setPlan(TestFlightMapper.toDto(flight.getPlan())));
    }
}
