package ashes.of.bomber.example.controllers;

import ashes.of.bomber.annotations.*;
import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.builder.TestSuiteBuilder;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.limiter.Limiter;
import ashes.of.bomber.core.stopwatch.Clock;
import ashes.of.bomber.core.stopwatch.Stopwatch;
import ashes.of.bomber.sink.histo.HistogramTimelinePrinter;
import ashes.of.bomber.sink.histo.HistogramTimelineSink;
import ashes.of.bomber.watcher.ProgressWatcher;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.temporal.ChronoUnit;
import java.util.Random;

@Throttle(threshold = 10)
@LoadTestSuite(name = "UserController", time = 5)
@WarmUp(disabled = true)
@Baseline(disabled = true)
public class UserControllerLoadTest {
    private static final Logger log = LogManager.getLogger();

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

    //    @BeforeEach
    public void beforeEach() {
        log.info("This method will be invoked before each test invocation");
    }

    //    @AfterEach
    public void afterEach() {
        log.info("This method will be invoked after each test invocation");
    }


    @SneakyThrows
    @LoadTest
    public void getUserByIdSync() {
        ResponseEntity<Void> response = webClient.get()
                .uri("/users/{id}", 1 + random.nextInt(1000))
                .retrieve()
                .toBodilessEntity()
                .block();

        log.debug("getUserByIdSync response status: {}", response.getStatusCode());
    }

    @LoadTest
    public void getUserByIdAsync(Clock clock) {
        Stopwatch getUsers = clock.stopwatch("getUsers");
        webClient.get()
                .uri("/users/{id}", 1 + random.nextInt(1000))
                .exchange()
                .doOnNext(response -> {
                    if (response.statusCode().isError())
                        throw new RuntimeException("invalid request");
                })
                .subscribe(response -> getUsers.success(), throwable -> getUsers.fail(throwable));
    }

    private static int port = 8080;

    public static void main(String... args) {
        port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;

//        runTestApp();
        runTestSuite();
    }

    private static void runTestSuite() {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        new TestSuiteBuilder<UserControllerLoadTest>()
                .app(app -> app.sink(new HistogramTimelineSink()))
                .name("UserController")
                .settings(b -> b
                        .warmUp(Settings::disabled)
                        .test(settings -> settings
                                .threadCount(1)
                                .time(10_000)) )

                .limiter(Limiter.withRate(1, 1000))
                .instance(() -> new UserControllerLoadTest(webClient))
                .beforeAll(UserControllerLoadTest::beforeAll)
//                .beforeEach(UserControllerLoadTest::beforeEach)
                .test("getUsersBlock", UserControllerLoadTest::getUserByIdSync)
                .test("getUsersAsync", UserControllerLoadTest::getUserByIdAsync)
//                .afterEach(UserControllerLoadTest::afterEach)
//                .afterAll(UserControllerLoadTest::afterAll)
                .build()
                .run();
    }

    private static void runTestApp() {
        HistogramTimelinePrinter printer = new HistogramTimelinePrinter(System.out, label -> !label.contains("UserController.getUsersAsync"));
        HistogramTimelineSink timeSink = new HistogramTimelineSink(printer, ChronoUnit.SECONDS);
        new TestAppBuilder()
                // log all times to console via log4j and HdrHistogram
//                .sink(new Log4jSink())
//                .sink(timeSink)
                .sink(timeSink)
                .watcher(3000, new ProgressWatcher())
                .limiter(Limiter.withRate(1, 100))

                // disabled baseline and warm-up stages
                .settings(b -> b
                        .baseline(Settings::disabled)
                        .warmUp(Settings::disabled)
                        .test(settings -> settings
                                .threadCount(1)
                                .time(10_000)))

                // add example test case via static init method
                .createSuite(UserControllerLoadTest::createSuite)
                .build()
                .run();
    }

    private static void createSuite(TestSuiteBuilder<UserControllerLoadTest> builder) {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        builder.name("UserController")
                .instance(() -> new UserControllerLoadTest(webClient))
                .beforeAll(UserControllerLoadTest::beforeAll)
//                .beforeEach(UserControllerLoadTest::beforeEach)
                .test("getUsersBlock", UserControllerLoadTest::getUserByIdSync)
                .test("getUsersAsync", UserControllerLoadTest::getUserByIdAsync)
//                .afterEach(UserControllerLoadTest::afterEach)
//                .afterAll(UserControllerLoadTest::afterAll)
        ;
    }
}
