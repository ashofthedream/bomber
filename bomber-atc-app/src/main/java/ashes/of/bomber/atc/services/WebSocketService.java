package ashes.of.bomber.atc.services;

import ashes.of.bomber.atc.dto.events.ActiveCarriersEvent;
import ashes.of.bomber.carrier.dto.carrier.CarrierDto;
import ashes.of.bomber.carrier.dto.events.SinkEvent;
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

    public void sendCarrierEvent(SinkEvent event) {
        sink.tryEmitNext(event);
    }
}
