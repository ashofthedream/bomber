package ashes.of.bomber.example.app.tests;

import ashes.of.bomber.annotations.AfterTestSuite;
import ashes.of.bomber.annotations.BeforeTestSuite;
import ashes.of.bomber.annotations.LoadTestCase;
import ashes.of.bomber.annotations.LoadTestSuite;
import ashes.of.bomber.tools.Stopwatch;
import ashes.of.bomber.tools.Tools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Random;

@LoadTestSuite(name = "AccountController")
public class AccountControllerLoadTest {
    private static final Logger log = LogManager.getLogger();

    private final Random random = new Random();
    private final WebClient webClient;

    public AccountControllerLoadTest(WebClient webClient) {
        this.webClient = webClient;
    }

    @BeforeTestSuite(onlyOnce = true)
    public void beforeAll() {
        log.info("This method will be invoked before all test");
    }

    @AfterTestSuite
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
                .exchangeToMono(response -> {
                    if (response.statusCode().isError())
                        throw new RuntimeException("invalid request");

                    return Mono.just(response);
                })
                .subscribe(response -> stopwatch.success(), stopwatch::fail);
    }
}
