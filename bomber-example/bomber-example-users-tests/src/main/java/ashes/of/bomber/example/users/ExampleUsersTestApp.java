package ashes.of.bomber.example.users;

import ashes.of.bomber.annotations.LoadTestApp;
import ashes.of.bomber.annotations.LoadTestSettings;
import ashes.of.bomber.annotations.Throttle;
import ashes.of.bomber.builder.BarrierBuilder;
import ashes.of.bomber.builder.BomberBuilder;
import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.configuration.Builder;
import ashes.of.bomber.example.users.client.UsersClient;
import ashes.of.bomber.example.users.tests.UserControllerLoadTest;
import ashes.of.bomber.example.utils.SleepUtils;
import ashes.of.bomber.sink.histogram.HistogramTimelinePrintStreamPrinter;
import ashes.of.bomber.sink.histogram.HistogramTimelineSink;
import ashes.of.bomber.squadron.Barrier;
import ashes.of.bomber.squadron.NoBarrier;
import ashes.of.bomber.squadron.zookeeper.ZookeeperBarrierBuilder;
import ashes.of.bomber.watcher.Log4jWatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.temporal.ChronoUnit;


/**
 * Example test application with some test cases built via builder and annotations
 */
@LoadTestApp(testSuites = {
        UserControllerLoadTest.class
})
@LoadTestSettings(threads = 2)
@Throttle(time = 20)
public class ExampleUsersTestApp {
    private static final Logger log = LogManager.getLogger();

    public static TestAppBuilder create(String appUrl, boolean barrier) {
        return TestAppBuilder.create(ExampleUsersTestApp.class)
                .config(config -> config.barrier(barrier ? new ZookeeperBarrierBuilder() : BarrierBuilder::noBarrier))
                .provide(UsersClient.class, () -> new UsersClient(appUrl))
                ;
    }


    public static void main(String... args) {
        var url = System.getenv().getOrDefault("EXAMPLE_USERS_APP_URL", "http://localhost:8082");
        var useBarrier = System.getenv().getOrDefault("EXAMPLE_USE_BARRIER", "false");

        var app = create(url, Boolean.parseBoolean(useBarrier));

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
