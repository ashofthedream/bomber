package ashes.of.bomber.atc.controllers;

import ashes.of.bomber.atc.dto.ws.WebSocketRequest;
import ashes.of.bomber.atc.services.InstanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;


@Component
public class ManagerWebSocketHandler implements WebSocketHandler {
    private static final Logger log = LogManager.getLogger();

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final InstanceService instanceService;

    public ManagerWebSocketHandler(InstanceService instanceService) {
        this.instanceService = instanceService;
    }

    private void process(WebSocketRequest message) {
        log.info("received message: {}", message);
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.info("handle session: {}", session.getId());
        Flux<WebSocketMessage> events = instanceService.events()
                .flatMap(this::encode)
                .map(session::textMessage);

        Flux<WebSocketRequest> receive = session.receive()
                .flatMap(s -> decode(s, WebSocketRequest.class))
                .doOnNext(this::process);

        return session.send(events).and(receive);
    }


    private <T> Mono<T> decode(WebSocketMessage message, Class<T> as) {
        try {
            T decoded = objectMapper.readValue(message.getPayloadAsText(), as);
            return Mono.just(decoded);
        } catch (IOException e) {
            log.warn("Can't decode message");
            return Mono.empty();
        }
    }

    private Mono<String> encode(Object event) {
        try {
            String encoded = objectMapper.writeValueAsString(event);
            return Mono.just(encoded);
        } catch (IOException e) {
            log.warn("Can't encode message");
            return Mono.empty();
        }
    }
}
