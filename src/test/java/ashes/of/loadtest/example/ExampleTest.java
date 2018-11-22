package ashes.of.loadtest.example;

import ashes.of.loadtest.builder.TestCaseBuilder;
import ashes.of.loadtest.builder.TestSuiteBuilder;
import ashes.of.loadtest.TestCase;
import ashes.of.loadtest.annotations.*;
import ashes.of.loadtest.settings.Settings;
import ashes.of.loadtest.sink.HdrHistogramSink;
import ashes.of.loadtest.sink.Log4jSink;
import ashes.of.loadtest.stopwatch.Stopwatch;
import ashes.of.loadtest.throttler.Limiter;
import org.junit.Test;


@Limit(time = 5)
@LoadTestCase(name = "example-test", time = 10)
@WarmUp(disabled = true)
@Baseline(disabled = true)
public class ExampleTest implements TestCase {

    private ExampleHttpClient client = new ExampleHttpClient("localhost", 8080);

    @LoadTest
    public void oneSlowRequest(Stopwatch stopwatch) throws Exception {
        client.someSlowRequest();
    }

    @LoadTest
    public void twoFastRequests(Stopwatch stopwatch) throws Exception {
        client.someFastRequest();
        stopwatch.record("someFastRequest");
        client.anotherFastRequest();
        stopwatch.record("anotherFastRequest");
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
                            .threads(1)
                            .time(20_000)))

                // add example test case via static init method
                .testCase(ExampleTest::init)
                .run();
    }

    @Test
    public void annotationsExample() {
        new TestSuiteBuilder()
                .sink(new Log4jSink())
                .sink(new HdrHistogramSink())

                // add example test case via annotations
                .testCase(ExampleTest.class)
                .run();
    }
}