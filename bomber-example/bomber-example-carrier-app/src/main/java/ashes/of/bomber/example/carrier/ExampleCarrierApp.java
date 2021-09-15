package ashes.of.bomber.example.carrier;

import ashes.of.bomber.carrier.starter.config.CarrierConfiguration;
import ashes.of.bomber.flight.plan.TestFlightPlan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Import(CarrierConfiguration.class)
public class ExampleCarrierApp {
    private static final Logger log = LogManager.getLogger();

    private final int port;

    public ExampleCarrierApp(@Value("${server.port}") int port) {
        this.port = port;
    }

    @PostConstruct
    public void init() {
        // run app via http
        log.info("BomberApp will be started via http after 5s");
        Executors.newSingleThreadScheduledExecutor()
                .schedule(this::startBomberAppViaHttp, 5, TimeUnit.SECONDS);
    }

    private void startBomberAppViaHttp() {
        if (true)
            return;

        WebClient client = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        log.info("try to start BomberApp via http");
        client.post()
                .uri("/applications/start")
                .body(BodyInserters.fromValue(new TestFlightPlan(System.currentTimeMillis() - 1630891500000L, List.of())))
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        response -> log.info("BomberApp stared"),
                        throwable -> log.warn("Failed to start BomberApp", throwable));
    }


    public static void main(String... args) {
        SpringApplication.run(ExampleCarrierApp.class, args);
    }
}
