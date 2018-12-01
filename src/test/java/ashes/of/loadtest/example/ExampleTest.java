package ashes.of.loadtest.example;

import ashes.of.loadtest.builder.TestCaseBuilder;
import ashes.of.loadtest.builder.TestSuiteBuilder;
import ashes.of.loadtest.TestCase;
import ashes.of.loadtest.annotations.*;
import ashes.of.loadtest.settings.Settings;
import ashes.of.loadtest.sink.HdrHistogramSink;
import ashes.of.loadtest.sink.Log4jSink;
import ashes.of.loadtest.stopwatch.Lap;
import ashes.of.loadtest.stopwatch.Stopwatch;
import ashes.of.loadtest.throttler.Limiter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;


@Limit(time = 5)
@LoadTestCase(name = "example-test", time = 20)
@Warmup(disabled = true)
@Baseline(disabled = true)
public class ExampleTest implements TestCase {
    private static final Logger log = LogManager.getLogger(ExampleTest.class);

    private final ExampleHttpClient client = new ExampleHttpClient("localhost", 8080);

    @Override
    @BeforeAll
    public void beforeAll() {
        log.info("This method will be invoked before all test");
    }

    @Override
    @AfterAll
    public void afterAll() {
        log.info("This method will be invoked after all test");
    }

    @Override
    @BeforeEach
    public void beforeEach() {
        log.info("This method will be invoked before each test invocation");
    }

    @Override
    @AfterEach
    public void afterEach() {
        log.info("This method will be invoked after each test invocation");
    }

    @LoadTest
    public void oneSlowRequest() throws Exception {
        client.someSlowRequest();
    }

    @LoadTest
    public void twoFastRequests(Stopwatch stopwatch) throws Exception {
        Lap someFastRequest = stopwatch.lap("someFastRequest");
        client.someFastRequest();
        someFastRequest.stop();

        Lap anotherFastRequest = stopwatch.lap("anotherFastRequest");
        client.anotherFastRequest();
        anotherFastRequest.stop();
    }


    public static void init(TestCaseBuilder<ExampleTest> builder) {
        builder .name("example_test")
                .testCase(ExampleTest::new)
                .test("one_slow_request", ExampleTest::oneSlowRequest)
                .test("two_fast_requests", ExampleTest::twoFastRequests);
    }


    @Test
    public void builderExample() {
        new TestSuiteBuilder()
                // log all times to console via log4j and HdrHistogram
                .sink(new Log4jSink())
                .sink(new HdrHistogramSink())
                .limiter(Limiter.withRate(1, 5_000))
                // disabled baseline and warm-up stages
                .settings(b -> b
                    .baseline(Settings::disabled)
                    .warmUp(Settings::disabled)
                    .test(settings -> settings
                            .threadCount(1)
                            .time(20_000)))

                // add example test case via static init method
                .testBuilder(ExampleTest::init)
                .run();
    }

    @Test
    public void annotationsExample() {
        new TestSuiteBuilder()
                .sink(new Log4jSink())
                .sink(new HdrHistogramSink())

                // add example test case via annotations
                .testClass(ExampleTest.class)
                .run();
    }
}