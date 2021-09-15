package ashes.of.bomber.example.accounts;

import ashes.of.bomber.builder.BomberBuilder;
import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.configuration.SettingsBuilder;
import ashes.of.bomber.example.accounts.tests.AccountControllerLoadTest;
import ashes.of.bomber.example.clients.AccountClient;
import ashes.of.bomber.example.utils.SleepUtils;
import ashes.of.bomber.limiter.RateLimiter;
import ashes.of.bomber.sink.histogram.HistogramTimelinePrintStreamPrinter;
import ashes.of.bomber.sink.histogram.HistogramTimelineSink;
import ashes.of.bomber.squadron.BarrierBuilder;
import ashes.of.bomber.squadron.NoBarrier;
import ashes.of.bomber.squadron.zookeeper.ZookeeperBarrierBuilder;
import ashes.of.bomber.watcher.Log4jWatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.temporal.ChronoUnit;


/**
 * Example test application with some test cases built via builder
 */
public class ExampleAccountsTestApp {
    private static final Logger log = LogManager.getLogger();

    public static TestAppBuilder create(String appUrl, int membersCount) {
        BarrierBuilder barrier = membersCount > 1 ?
                new ZookeeperBarrierBuilder().members(membersCount) : NoBarrier::new;

        return new TestAppBuilder()
                .name("ExampleAccountsTestApp")
                .config(config -> config
                        .settings(new SettingsBuilder()
                                .setThreadsCount(2)
                                .setSeconds(10)
                                .build())
                        .barrier(barrier)
                        .limiter(new RateLimiter(10, Duration.ofSeconds(1))))
                .createSuite(AccountControllerLoadTest::create, new AccountClient(appUrl));
    }

    public static void main(String... args) {
        var url = System.getenv().getOrDefault("EXAMPLE_ACCOUNTS_APP_URL", "http://localhost:8083");
        var membersCount = System.getenv().get("EXAMPLE_BARRIER_MEMBERS");

        var app = create(url, membersCount != null ? Integer.parseInt(membersCount) : 0);

        var reports = new BomberBuilder()
                // log all times to console via log4j and HdrHistogram
                // .sink(new Log4jSink())
                .sink(new HistogramTimelineSink(ChronoUnit.SECONDS, new HistogramTimelinePrintStreamPrinter()))
                // .sink(new HistogramSink())
                .watcher(new Log4jWatcher())
                .add(app)
                .build()
                .start();


        var report = reports.getTestApps()
                .stream()
                .findFirst()
                .orElseThrow();

        log.info("test report for flight: {}", -1);
        report.getTestSuites()
                .forEach(testSuite -> {
                    log.debug("TestSuite name: {}", testSuite.getName());
                    testSuite.getTestCases()
                            .forEach(testCase -> {
                                log.debug("TestCase name: {}, total iterations: {}, errors: {}, total time elapsed: {}ms",
                                        testCase.getName(),
                                        testCase.getTotalIterationsCount(),
                                        testCase.getTotalErrorsCount(),
                                        testCase.getTotalTimeElapsed());
                            });
                });

        SleepUtils.sleepQuietlyExact(1_000);
    }
}
