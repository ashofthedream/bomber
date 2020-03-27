package ashes.of.bomber.example.app.tests;

import ashes.of.bomber.annotations.*;
import ashes.of.bomber.stopwatch.Tools;
import ashes.of.bomber.stopwatch.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Random;

@LoadTestSuite(name = "AccountController")
public class AccountControllerLoadTest {
    private static final Logger log = LogManager.getLogger();

    private final Random random = new Random();
    private final WebClient webClient;

    public AccountControllerLoadTest(WebClient webClient) {
        this.webClient = webClient;
    }

    @BeforeAll(onlyOnce = true)
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
    public void getAccountByIdAsync(Tools tools) {
        Stopwatch stopwatch = tools.stopwatch("getAccounts");
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
