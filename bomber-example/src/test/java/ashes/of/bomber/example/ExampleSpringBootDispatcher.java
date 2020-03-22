package ashes.of.bomber.example;

import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.core.Application;
import ashes.of.bomber.dispatcher.starter.builder.SpringBootDispatcherBuilder;
import ashes.of.bomber.example.controllers.AccountControllerLoadTest;
import ashes.of.bomber.example.controllers.UserControllerLoadTest;
import ashes.of.bomber.sink.histo.HistogramSink;
import ashes.of.bomber.sink.histo.HistogramTimelineSink;
import ashes.of.bomber.squadron.BarrierBuilder;
import ashes.of.bomber.squadron.NoBarrier;
import ashes.of.bomber.squadron.zookeeper.ZookeeperBarrierBuilder;
import ashes.of.bomber.watcher.ProgressWatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class ExampleSpringBootDispatcher {
    private static final Logger log = LogManager.getLogger();

    public static void main(String... args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        int members = args.length > 1 ? Integer.parseInt(args[1]) : 1;

        WebClient testClient = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        BarrierBuilder barrier = members > 1 ? new ZookeeperBarrierBuilder().members(members) : new NoBarrier.Builder();

        Application app = new TestAppBuilder()
                .dispatcher(new SpringBootDispatcherBuilder())
                // log all times to console via log4j and HdrHistogram
//                .sink(new Log4jSink())
                .sink(new HistogramTimelineSink(ChronoUnit.SECONDS, System.out))
                .sink(new HistogramSink())
                .barrier(barrier)
                .watcher(1000, new ProgressWatcher())


                // add example test suite via static init method
                .testSuiteClass(UserControllerLoadTest.class, new Class[]{WebClient.class}, testClient)

                // add second test suite via annotations
                .testSuiteClass(AccountControllerLoadTest.class, new Class[]{WebClient.class}, testClient)

                .application();


        WebClient dispatcherClient = WebClient.builder()
                .baseUrl("http://localhost:9000")
                .build();

        // run app via http
        Executors.newSingleThreadScheduledExecutor()
                .schedule(() -> {
                    log.info("try to start TestApp via http");
                    dispatcherClient.post()
                            .uri("/dispatcher/run")
                            .retrieve()
                            .toBodilessEntity()
                            .subscribe(response -> log.info("TestApp stared"), throwable -> log.warn("Failed to start TestApp", throwable));

                }, 5, TimeUnit.SECONDS);

        app.await();
    }
}
