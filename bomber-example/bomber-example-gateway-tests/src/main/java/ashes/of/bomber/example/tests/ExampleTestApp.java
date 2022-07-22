package ashes.of.bomber.example.tests;

import ashes.of.bomber.Bomber;
import ashes.of.bomber.builder.BomberBuilder;
import ashes.of.bomber.example.accounts.ExampleAccountsTestApp;
import ashes.of.bomber.example.users.ExampleUsersTestApp;
import ashes.of.bomber.example.utils.SleepUtils;
import ashes.of.bomber.sink.histogram.HistogramTimelinePrintStreamPrinter;
import ashes.of.bomber.sink.histogram.HistogramTimelineSink;
import ashes.of.bomber.watcher.Log4jWatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.temporal.ChronoUnit;


public class ExampleTestApp {
    private static final Logger log = LogManager.getLogger();

    public static Bomber create(String appUrl, boolean barrier) {
        var accountsApp = ExampleAccountsTestApp.create(appUrl, barrier);
        var usersApp = ExampleUsersTestApp.create(appUrl, barrier);

        return new BomberBuilder()
                // log all times to console via log4j and HdrHistogram
                // .sink(new Log4jSink())
                .sink(new HistogramTimelineSink(ChronoUnit.SECONDS, new HistogramTimelinePrintStreamPrinter()))
                // .sink(new HistogramSink())
                .watcher(new Log4jWatcher())
                .add(usersApp)
                .add(accountsApp)
                .build();
    }


    public static void main(String... args) {
        var appUrl = System.getenv().getOrDefault("EXAMPLE_APP_URL", "http://localhost:8081");
        var useBarrier = System.getenv().getOrDefault("EXAMPLE_USE_BARRIER", "false");

        var report = ExampleTestApp.create(appUrl, Boolean.parseBoolean(useBarrier))
                .start();

        report.testApps()
                .forEach(testApp -> {
                    log.info("test report for flight: {}", -1);
                    testApp.testSuites()
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

                });


        SleepUtils.sleepQuietlyExact(1_000);
    }
}
