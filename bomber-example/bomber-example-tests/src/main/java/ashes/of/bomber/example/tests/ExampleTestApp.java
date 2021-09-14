package ashes.of.bomber.example.tests;


import ashes.of.bomber.annotations.LoadTestApp;
import ashes.of.bomber.builder.BomberBuilder;
import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.example.accounts.ExampleAccountsTestApp;
import ashes.of.bomber.example.clients.UsersClient;
import ashes.of.bomber.example.users.ExampleUsersTestApp;
import ashes.of.bomber.example.utils.SleepUtils;
import ashes.of.bomber.sink.histogram.HistogramTimelinePrintStreamPrinter;
import ashes.of.bomber.sink.histogram.HistogramTimelineSink;
import ashes.of.bomber.squadron.BarrierBuilder;
import ashes.of.bomber.squadron.NoBarrier;
import ashes.of.bomber.squadron.zookeeper.ZookeeperBarrierBuilder;
import ashes.of.bomber.watcher.Log4jWatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.temporal.ChronoUnit;


public class ExampleTestApp {
    private static final Logger log = LogManager.getLogger();

//    public static TestAppBuilder create(String appUrl, int membersCount) {
//        BarrierBuilder barrier = membersCount > 1 ?
//                new ZookeeperBarrierBuilder().members(membersCount) :
//                new NoBarrier.Builder();
//
//        return TestAppBuilder.create(ExampleTestApp.class)
//                .config(config -> config.barrier(barrier))
//                .provide(UsersClient.class, () -> new UsersClient(appUrl))
//                ;
//    }


    public static void main(String... args) {
        var appUrl = System.getenv().getOrDefault("EXAMPLE_APP_URL", "http://localhost:8081");
        var membersCount = System.getenv().get("EXAMPLE_BARRIER_MEMBERS");

        var accountsApp = ExampleAccountsTestApp.create(appUrl, membersCount != null ? Integer.parseInt(membersCount) : 0);
        var usersApp = ExampleUsersTestApp.create(appUrl, membersCount != null ? Integer.parseInt(membersCount) : 0);

        var reports = new BomberBuilder()
                // log all times to console via log4j and HdrHistogram
                // .sink(new Log4jSink())
                .sink(new HistogramTimelineSink(ChronoUnit.SECONDS, new HistogramTimelinePrintStreamPrinter()))
                // .sink(new HistogramSink())
                .watcher(new Log4jWatcher())
                .add(usersApp)
                .add(accountsApp)
                .build()
                .start();

        reports.stream().forEach(report -> {
            log.info("test report for flight: {}", report.getPlan().getFlightId());
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

        });


        SleepUtils.sleepQuietlyExact(1_000);
    }
}
