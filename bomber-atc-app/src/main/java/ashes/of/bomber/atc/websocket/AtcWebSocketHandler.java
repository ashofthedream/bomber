package ashes.of.bomber.atc.websocket;

import ashes.of.bomber.atc.services.WebSocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AtcWebSocketHandler implements WebSocketHandler {
    private static final Logger log = LogManager.getLogger();

    private final WebSocketService service;

    public AtcWebSocketHandler(WebSocketService service) {
        this.service = service;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return Mono.when(receive(session), send(session));
    }

    private Flux<WebSocketMessage> receive(WebSocketSession session) {
        return session.receive()
                .doOnNext(in -> log.debug("Received via ws message: {}", in.getPayloadAsText()));
    }

    private Mono<Void> send(WebSocketSession session) {
        return session.send(service.events()
                .flatMap(event -> {
                    try {
                        var mapper = new ObjectMapper();
                        var value = mapper.writeValueAsString(event);

                        return Mono.just(value);
                    } catch (Exception e) {
                        return Mono.empty();
                    }
                })
                .map(session::textMessage));
    }
}
