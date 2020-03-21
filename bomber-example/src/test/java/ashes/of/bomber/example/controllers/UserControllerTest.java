package ashes.of.bomber.example.controllers;

import ashes.of.bomber.annotations.*;
import ashes.of.bomber.builder.TestSuiteBuilder;
import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.limiter.Limiter;
import ashes.of.bomber.core.stopwatch.Stopwatch;
import ashes.of.bomber.core.stopwatch.Clock;
import ashes.of.bomber.sink.histo.HistogramSink;
import ashes.of.bomber.sink.histo.HistogramTimelinePrinter;
import ashes.of.bomber.sink.histo.HistogramTimelineSink;
import ashes.of.bomber.sink.Log4jSink;
import ashes.of.bomber.watcher.ProgressWatcher;
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

import java.time.temporal.ChronoUnit;
import java.util.Random;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserControllerTest {
    private static final Logger log = LogManager.getLogger();

    @Throttle(time = 5)
    @LoadTestSuite(name = "example-test", time = 20)
    @WarmUp(disabled = true)
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

        public void getUsersBlock() {
            log.debug("time");
            ClientResponse response = webClient.get()
                    .uri("/users/{id}", 1 + random.nextInt(1000))
                    .exchange()
                    .block();

            log.debug("getUsers: {}", response.statusCode());
        }

        public void getUsersSubscribe(Clock clock) {
            Stopwatch sw = clock.stopwatch("getUsers");
            webClient.get()
                    .uri("/users/{id}", 1 + random.nextInt(1000))
                    .exchange()
                    .doOnNext(response -> {
                        if (response.statusCode().isError())
                            throw new RuntimeException("invalid request");
                    })
                    .subscribe(response -> {
                        log.debug("getUsers success: {}", response.statusCode());
                        sw.success();
                    }, throwable -> {
                        log.debug("getUsers failure", throwable);
                        sw.fail(throwable);
                    });
        }
    }

    @LocalServerPort
    private int port;


    @Test
    public void performanceTestWithBuilder() throws Exception {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        HistogramTimelinePrinter printer = new HistogramTimelinePrinter(System.out, label -> !label.contains("getUsersSubscribe"));

        HistogramTimelineSink timeSink = new HistogramTimelineSink(printer, ChronoUnit.SECONDS);
        new TestAppBuilder()
                // log all times to console via log4j and HdrHistogram
//                .sink(new Log4jSink())
//                .sink(timeSink)
                .sink(new HistogramSink())
                .watcher(new ProgressWatcher())
                .sharedLimiter(Limiter.withRate(1, 100))

                // disabled baseline and warm-up stages
                .settings(b -> b
                        .baseline(Settings::disabled)
                        .warmUp(Settings::disabled)
                        .test(settings -> settings
                                .threadCount(1)
                                .time(10_000)))

                // add example test case via static init method
                .createSuite(this::cteateUserControllerLoadTestSuite)
                .build()
                .run();

        Thread.sleep(10_000);
    }

    private void cteateUserControllerLoadTestSuite(TestSuiteBuilder<UserControllerLoadTest> builder) {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        builder .name("example_test")
                .instance(() -> new UserControllerLoadTest(webClient))
                .beforeAll(UserControllerLoadTest::beforeAll)
//                .beforeEach(UserControllerLoadTest::beforeEach)
//                .test("getUserById", UserControllerLoadTest::getUserById)
//                .test("getUsersBlock", UserControllerLoadTest::getUsersBlock)
                .test("getUsersSubscribe", UserControllerLoadTest::getUsersSubscribe)
//                .afterEach(UserControllerLoadTest::afterEach)
//                .afterAll(UserControllerLoadTest::afterAll)
        ;
    }

    @Test
    @Ignore
    public void runAnnotationsExample() {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        new TestAppBuilder()
                .sink(new Log4jSink())
                .sink(new HistogramTimelineSink())

                // add example test case via annotations
                .testSuiteClass(UserControllerLoadTest.class, new Class[] { WebClient.class }, webClient)
                .build()
                .run();
    }
}
