package ashes.of.bomber.atc.controllers;

import ashes.of.bomber.atc.dto.LoginRequest;
import ashes.of.bomber.atc.utils.HttpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.time.Duration;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ManagerWebSocketHandlerTest {
    private static final Logger log = LogManager.getLogger();

    @LocalServerPort
    private int port;

    @Ignore
    @Test
    public void testWebSocket() {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(HttpUtils::logRequest)
                .build();


        String sessionId = webClient.post()
                .uri("/manager/login")
                .body(BodyInserters.fromObject(new LoginRequest("admin", "admin")))
                .exchange()
                .doOnSuccess(HttpUtils::logResponse)
                .map(ClientResponse::cookies)
                .map(c -> {
                    ResponseCookie session = c.getFirst("SESSION");
                    return session != null ? session.getValue() : "";
                })
                .blockOptional()
                .orElse(null);



        Flux.interval(Duration.ofSeconds(5))
                .subscribeOn(Schedulers.elastic())
                .flatMap(tick ->
                        webClient.post()
                                .uri("/instances/temp/createInstance")
                                .header("Cookie", "SESSION=" + sessionId)
                                .body(BodyInserters.fromObject("{\"id\": 1337, \"tick\": \"" + tick + "\"}"))
                                .exchange())
                .subscribe(rsp -> {
                    log.info("sent, code: {}", rsp.statusCode());
                });


        URI uri = URI.create("ws://localhost:" + port + "/socket");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "SESSION=" + sessionId);

        WebSocketClient client = new ReactorNettyWebSocketClient();
        client.execute(uri, headers, this::loop)
                .block();
    }

    private Mono<Void> loop(WebSocketSession session) {
//        log.info("session start");
//        Flux<WebSocketMessage> ticker = Flux.interval(Duration.ofSeconds(2))
//                .map(this::tickToRequest)
                ;

        return session.receive()
                .doOnNext(this::receive)
                .then();

//        return session.send(ticker)
//                .and(session.receive().doOnNext(this::receive));
    }

    private void receive(WebSocketMessage message) {
        log.info(">> {}", message.getPayloadAsText());
    }


    private WebSocketMessage tickToRequest(Long tick) {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        String json = String.format("{\"id\": %d, \"type\": \"ping\"}", tick);
        DefaultDataBuffer buffer = factory.wrap(json.getBytes());
        log.info("send message: {}", json);

        return new WebSocketMessage(WebSocketMessage.Type.TEXT, buffer);
    }
}
