package ashes.of.bomber.example.controllers;

import ashes.of.bomber.annotations.*;
import ashes.of.bomber.core.stopwatch.Clock;
import ashes.of.bomber.core.stopwatch.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Random;

@Throttle(threshold = 10)
@LoadTestSuite(name = "AccountController", time = 5)
@WarmUp(disabled = true)
@Baseline(disabled = true)
public class AccountControllerLoadTest {
    private static final Logger log = LogManager.getLogger();

    private final Random random = new Random();
    private final WebClient webClient;

    public AccountControllerLoadTest(WebClient webClient) {
        this.webClient = webClient;
    }

    @BeforeAll
    public void beforeAll() {
        log.info("This method will be invoked before all test");
    }

    @AfterAll
    public void afterAll() {
        log.info("This method will be invoked after all test");
    }

    @LoadTestCase
    public void getAccountByIdSync() {
        ResponseEntity<Void> response = webClient.get()
                .uri("/accounts/{id}", 1 + random.nextInt(1000))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @LoadTestCase(async = true)
    public void getAccountByIdAsync(Clock clock) {
        Stopwatch stopwatch = clock.stopwatch("getAccounts");
        webClient.get()
                .uri("/accounts/{id}", 1 + random.nextInt(1000))
                .exchange()
                .doOnNext(response -> {
                    if (response.statusCode().isError())
                        throw new RuntimeException("invalid request");
                })
                .subscribe(response -> stopwatch.success(), stopwatch::fail);
    }
}