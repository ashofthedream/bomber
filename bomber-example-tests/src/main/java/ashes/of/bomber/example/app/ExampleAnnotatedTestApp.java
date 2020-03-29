package ashes.of.bomber.example.app;

import ashes.of.bomber.annotations.*;
import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.example.app.tests.AccountControllerLoadTest;
import ashes.of.bomber.example.app.tests.UserControllerLoadTest;
import ashes.of.bomber.sink.histogram.HistogramSink;
import ashes.of.bomber.sink.histogram.HistogramTimelineSink;
import ashes.of.bomber.squadron.BarrierBuilder;
import ashes.of.bomber.squadron.NoBarrier;
import ashes.of.bomber.squadron.zookeeper.ZookeeperBarrierBuilder;
import ashes.of.bomber.watcher.Log4jWatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.temporal.ChronoUnit;


/**
 * Example test application with some test cases built via builder and annotations
 */
@LoadTestApp(testSuites = {
        UserControllerLoadTest.class,
        AccountControllerLoadTest.class
})
@LoadTest(threads = 2, time = 10)
@Throttle(time = 1)
public class ExampleAnnotatedTestApp {
    private static final Logger log = LogManager.getLogger();

    private static String url;

    @Provide
    public WebClient webClient() {
        log.error("provide webClient");
        return WebClient.builder()
                .baseUrl(url)
                .build();
    }

    public static void main(String... args) {
        url = args.length > 0 ? args[0] : "http://localhost:8080";
        int members = args.length > 1 ? Integer.parseInt(args[1]) : 1;

        BarrierBuilder barrier = members > 1 ? new ZookeeperBarrierBuilder().members(members) : new NoBarrier.Builder();

        TestAppBuilder.create(ExampleAnnotatedTestApp.class)
                // log all times to console via log4j and HdrHistogram
//                .sink(new Log4jSink())
                .sink(new HistogramTimelineSink(ChronoUnit.SECONDS, System.out))
                .sink(new HistogramSink())
                .watcher(1000, new Log4jWatcher())
                .barrier(barrier)
                .build()
                .start();
    }
}
