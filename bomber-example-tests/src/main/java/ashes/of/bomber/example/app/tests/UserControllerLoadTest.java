package ashes.of.bomber.example.app.tests;

import ashes.of.bomber.annotations.AfterTestSuite;
import ashes.of.bomber.annotations.BeforeTestSuite;
import ashes.of.bomber.annotations.LoadTest;
import ashes.of.bomber.annotations.LoadTestCase;
import ashes.of.bomber.annotations.LoadTestSuite;
import ashes.of.bomber.annotations.Throttle;
import ashes.of.bomber.tools.Stopwatch;
import ashes.of.bomber.tools.Tools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Random;

@LoadTestSuite(name = "UserController")
@LoadTest(time = 10)
@Throttle(threshold = 10, shared = true)
public class UserControllerLoadTest {
    private static final Logger log = LogManager.getLogger();

    private final Random random = new Random();
    private final WebClient webClient;

    public UserControllerLoadTest(WebClient webClient) {
        this.webClient = webClient;
    }

    @BeforeTestSuite
    public void beforeAll() {
        log.info("This method will be invoked before all test");
    }

    @AfterTestSuite
    public void afterAll() {
        log.info("This method will be invoked after all test");
    }

//    @BeforeEach
    public void beforeEach() {
        log.info("This method will be invoked before each test invocation");
    }

//    @AfterEach
    public void afterEach() {
        log.info("This method will be invoked after each test invocation");
    }


    @LoadTestCase
    public void getUserByIdSync() {
        ResponseEntity<Void> response = webClient.get()
                .uri("/users/{id}/{testId}", 1 + random.nextInt(1000), "getUserByIdSync")
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @LoadTestCase(async = true)
    public void getUserByIdAsync(Tools tools) {
        Stopwatch stopwatch = tools.stopwatch("getUsers");
        webClient.get()
                .uri("/users/{id}/{testId}", 1 + random.nextInt(1000), "getUserByIdAsync")
                .exchangeToMono(response -> {
                    if (response.statusCode().isError())
                        throw new RuntimeException("invalid request");

                    return Mono.just(response);
                })
                .subscribe(response -> stopwatch.success(), stopwatch::fail);
    }
}
