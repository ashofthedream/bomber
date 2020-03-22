package ashes.of.bomber.example.dispatcher;

import ashes.of.bomber.core.BomberApp;
import ashes.of.bomber.dispatcher.starter.config.DispatcherConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Import(DispatcherConfig.class)
public class DispatcherExampleApp {
    private static final Logger log = LogManager.getLogger();

    private final int port;

    public DispatcherExampleApp(@Value("${server.port}") int port) {
        this.port = port;
    }

    @PostConstruct
    public void init() {
        log.info("BomberApp will be started via http after 5s");
        // run app via http
        Executors.newSingleThreadScheduledExecutor()
                .schedule(this::startBomberAppViaHttp, 5, TimeUnit.SECONDS);
    }

    private void startBomberAppViaHttp() {
        WebClient dispatcherClient = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        log.info("try to start BomberApp via http");
        dispatcherClient.post()
                .uri("/application/run")
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        response -> log.info("BomberApp stared"),
                        throwable -> log.warn("Failed to start BomberApp", throwable));
    }


    public static void main(String... args) {
        SpringApplication.run(DispatcherExampleApp.class, args);
    }
}
