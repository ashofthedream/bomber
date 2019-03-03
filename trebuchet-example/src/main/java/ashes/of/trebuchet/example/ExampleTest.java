package ashes.of.trebuchet.example;

import ashes.of.trebuchet.builder.TestCaseBuilder;
import ashes.of.trebuchet.builder.TestSuiteBuilder;
import ashes.of.trebuchet.annotations.*;
import ashes.of.trebuchet.builder.Settings;
import ashes.of.trebuchet.sink.HistogramSink;
import ashes.of.trebuchet.sink.Log4jSink;
import ashes.of.trebuchet.stopwatch.Lap;
import ashes.of.trebuchet.stopwatch.Stopwatch;
import ashes.of.trebuchet.limiter.Limiter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Limit(time = 5)
@LoadTestCase(name = "example-test", time = 20)
@Warmup(disabled = true)
@Baseline(disabled = true)
public class ExampleTest {
    private static final Logger log = LogManager.getLogger(ExampleTest.class);

    private final ExampleHttpClient client = new ExampleHttpClient("localhost", 8080);

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

    @LoadTest
    public void oneSlowRequest() throws Exception {
        client.someSlowRequest();
    }

    @LoadTest
    public void twoFastRequests(Stopwatch stopwatch) throws Exception {
        Lap someFastRequest = stopwatch.lap("someFastRequest");
        client.someFastRequest();
        someFastRequest.elapsed();

        Lap anotherFastRequest = stopwatch.lap("anotherFastRequest");
        client.anotherFastRequest();
        anotherFastRequest.elapsed();
    }


    private static void init(TestCaseBuilder<ExampleTest> builder) {
        builder .name("example_test")
                .testCase(ExampleTest::new)
                .beforeAll(ExampleTest::beforeAll)
                .beforeEach(ExampleTest::beforeEach)
                .test("one_slow_request", ExampleTest::oneSlowRequest)
                .test("two_fast_requests", ExampleTest::twoFastRequests)
                .afterEach(ExampleTest::afterEach)
                .afterAll(ExampleTest::afterAll);
    }


    public void runBuilderExample() {
        new TestSuiteBuilder()
                // log all times to console via log4j and HdrHistogram
                .sink(new Log4jSink())
                .sink(new HistogramSink())
                .limiter(Limiter.withRate(1, 5_000))
                // disabled baseline and warm-up stages
                .settings(b -> b
                    .baseline(Settings::disabled)
                    .warmUp(Settings::disabled)
                    .test(settings -> settings
                            .threadCount(1)
                            .time(20_000)))

                // add example test case via static init method
                .addBuilder(ExampleTest::init)
                .build()
                .run();
    }

    public void runAnnotationsExample() {
        new TestSuiteBuilder()
                .sink(new Log4jSink())
                .sink(new HistogramSink())

                // add example test case via annotations
                .addClass(ExampleTest.class)
                .build()
                .run();
    }

    public static void main(String... args) {
        ExampleTest test = new ExampleTest();
        test.runBuilderExample();
        test.runAnnotationsExample();
    }
}