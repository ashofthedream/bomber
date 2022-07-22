package ashes.of.bomber.example.accounts;

import ashes.of.bomber.builder.BarrierBuilder;
import ashes.of.bomber.builder.BomberBuilder;
import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.builder.SettingsBuilder;
import ashes.of.bomber.configuration.Builder;
import ashes.of.bomber.example.accounts.client.AccountClient;
import ashes.of.bomber.example.accounts.tests.AccountControllerLoadTest;
import ashes.of.bomber.example.utils.SleepUtils;
import ashes.of.bomber.limiter.RateLimiter;
import ashes.of.bomber.sink.histogram.HistogramTimelinePrintStreamPrinter;
import ashes.of.bomber.sink.histogram.HistogramTimelineSink;
import ashes.of.bomber.squadron.Barrier;
import ashes.of.bomber.squadron.NoBarrier;
import ashes.of.bomber.squadron.zookeeper.ZookeeperBarrierBuilder;
import ashes.of.bomber.watcher.Log4jWatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;


/**
 * Example test application with some test cases built via builder
 */
public class ExampleAccountsTestApp implements Supplier<TestAppBuilder> {
    private static final Logger log = LogManager.getLogger();

    public static TestAppBuilder create(String appUrl, boolean barrier) {

        return new TestAppBuilder()
                .name("ExampleAccountsTestApp")
                .config(config -> config
                        .settings(new SettingsBuilder()
                                .setThreads(2)
                                .setSeconds(10))
                        .barrier(barrier ? new ZookeeperBarrierBuilder() : BarrierBuilder::noBarrier)
                        .limiter(new RateLimiter(10, Duration.ofSeconds(1))))
                .createSuite(AccountControllerLoadTest::create, new AccountClient(appUrl));
    }

    @Override
    public TestAppBuilder get() {
        var url = System.getenv().getOrDefault("EXAMPLE_ACCOUNTS_APP_URL", "http://localhost:8083");
        var useBarrier = System.getenv().getOrDefault("EXAMPLE_USE_BARRIER", "false");

        return create(url, Boolean.parseBoolean(useBarrier));
    }

    public static void main(String... args) {
        var app = new ExampleAccountsTestApp().get();

        var reports = new BomberBuilder()
                // log all times to console via log4j and HdrHistogram
                // .sink(new Log4jSink())
                .sink(new HistogramTimelineSink(ChronoUnit.SECONDS, new HistogramTimelinePrintStreamPrinter()))
                // .sink(new HistogramSink())
                .watcher(new Log4jWatcher())
                .add(app)
                .build()
                .start();


        var report = reports.testApps()
                .stream()
                .findFirst()
                .orElseThrow();

        log.info("test report for flight: {}", -1);
        report.testSuites()
                .forEach(testSuite -> {
                    log.debug("TestSuite name: {}", testSuite.name());
                    testSuite.testCases()
                            .forEach(testCase -> {
                                log.debug("TestCase name: {}, total iterations: {}, errors: {}, total time elapsed: {}ms",
                                        testCase.name(),
                                        testCase.iterationsCount(),
                                        testCase.errorsCount(),
                                        testCase.totalTimeElapsed());
                            });
                });

        SleepUtils.sleepQuietlyExact(1_000);
    }
}
