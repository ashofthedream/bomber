package ashes.of.bomber.atc.websocket;

import ashes.of.bomber.atc.services.WebSocketService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.web.reactive.socket.WebSocketMessage.Type.TEXT;

@Component
public class AtcWebSocketHandler implements WebSocketHandler {
    private static final Logger log = LogManager.getLogger();

    private final WebSocketService service;
    private final Jackson2JsonEncoder encoder = new Jackson2JsonEncoder();
    private final Jackson2JsonDecoder decoder = new Jackson2JsonDecoder();

    public AtcWebSocketHandler(WebSocketService service) {
        this.service = service;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return Mono.when(receive(session), send(session));
    }

    private Flux<Object> receive(WebSocketSession session) {
        return session.receive()
                .map(WebSocketMessage::getPayload)
                .map(this::decode)
                .doOnNext(message -> log.debug("Received via ws message: {}", message));
    }

    private Mono<Void> send(WebSocketSession session) {
        return service.events()
                .flatMap(event -> encode(session, event))
                .map(buffer -> new WebSocketMessage(TEXT, buffer))
                .as(session::send);
    }


    private Flux<DataBuffer> encode(WebSocketSession session, Object event) {
        return encoder.encode(
                Mono.just(event),
                session.bufferFactory(),
                ResolvableType.forType(Object.class),
                MediaType.APPLICATION_JSON,
                Map.of());
    }

    private Object decode(DataBuffer buffer) {
        return decoder.decode(
                buffer,
                ResolvableType.forType(Object.class),
                MediaType.APPLICATION_JSON,
                Map.of()
        );
    }
}
