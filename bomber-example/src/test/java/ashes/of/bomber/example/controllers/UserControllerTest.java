package ashes.of.bomber.example.controllers;

import ashes.of.bomber.annotations.*;
import ashes.of.bomber.builder.TestCaseBuilder;
import ashes.of.bomber.builder.TestSuiteBuilder;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.limiter.Limiter;
import ashes.of.bomber.example.model.User;
import ashes.of.bomber.sink.HistogramSink;
import ashes.of.bomber.sink.Log4jSink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Random;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserControllerTest {
    private static final Logger log = LogManager.getLogger();

    @Limit(time = 5)
    @LoadTestCase(name = "example-test", time = 20)
    @Warmup(disabled = true)
    @Baseline(disabled = true)
    public static class UserControllerLoadTest {
        private final Random random = new Random();
        private final WebClient webClient;

        public UserControllerLoadTest(WebClient webClient) {
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

        @BeforeEach
        public void beforeEach() {
            log.info("This method will be invoked before each test invocation");
        }

        @AfterEach
        public void afterEach() {
            log.info("This method will be invoked after each test invocation");
        }


        public void getUserById() {
            ClientResponse response = webClient.get()
                    .uri("/users/{id}", 1 + random.nextInt(1000))
                    .exchange()
                    .block();

            log.debug("getUserById: {}", response.statusCode());
        }

        public void getUsers() {
            ClientResponse response = webClient.get()
                    .uri("/users/{id}", 1 + random.nextInt(1000))
                    .exchange()
                    .block();

            log.debug("getUsers: {}", response.statusCode());
        }
    }

    @LocalServerPort
    private int port;


    @Test
    public void performanceTestWithBuilder() {

        new TestSuiteBuilder()
                // log all times to console via log4j and HdrHistogram
                .sink(new Log4jSink())
                .sink(new HistogramSink())
                .limiter(Limiter.withRate(1, 100))
                // disabled baseline and warm-up stages
                .settings(b -> b
                        .baseline(Settings::disabled)
                        .warmUp(Settings::disabled)
                        .test(settings -> settings
                                .threadCount(1)
                                .time(10_000)))

                // add example test case via static init method
                .addBuilder(this::init)
                .build()
                .run();
    }

    private void init(TestCaseBuilder<UserControllerLoadTest> builder) {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        builder.name("example_test")
                .testCase(() -> new UserControllerLoadTest(webClient))
                .beforeAll(UserControllerLoadTest::beforeAll)
                .beforeEach(UserControllerLoadTest::beforeEach)
                .test("getUserById", UserControllerLoadTest::getUserById)
                .test("getUsers", UserControllerLoadTest::getUsers)
                .afterEach(UserControllerLoadTest::afterEach)
                .afterAll(UserControllerLoadTest::afterAll);
    }

    @Test
    @Ignore
    public void runAnnotationsExample() {
        new TestSuiteBuilder()
                .sink(new Log4jSink())
                .sink(new HistogramSink())

                // add example test case via annotations
                .addClass(UserControllerLoadTest.class)
                .build()
                .run();
    }
}
