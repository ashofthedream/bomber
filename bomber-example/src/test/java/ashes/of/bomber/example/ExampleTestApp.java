package ashes.of.bomber.example;

import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.builder.TestSuiteBuilder;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.limiter.Limiter;
import ashes.of.bomber.example.controllers.AccountControllerLoadTest;
import ashes.of.bomber.example.controllers.UserControllerLoadTest;
import ashes.of.bomber.sink.histo.HistogramSink;
import ashes.of.bomber.sink.histo.HistogramTimelineSink;
import ashes.of.bomber.squadron.BarrierBuilder;
import ashes.of.bomber.squadron.NoBarrier;
import ashes.of.bomber.squadron.zookeeper.ZookeeperBarrierBuilder;
import ashes.of.bomber.watcher.ProgressWatcher;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.temporal.ChronoUnit;

public class ExampleTestApp {

    public static void main(String... args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        int members = args.length > 1 ? Integer.parseInt(args[1]) : 1;

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        BarrierBuilder barrier = members > 1 ? new ZookeeperBarrierBuilder().members(members) : new NoBarrier.Builder();

        new TestAppBuilder()
                // log all times to console via log4j and HdrHistogram
//                .sink(new Log4jSink())
                .sink(new HistogramTimelineSink(ChronoUnit.SECONDS, System.out))
                .sink(new HistogramSink())
                .barrier(barrier)
                .watcher(1000, new ProgressWatcher())
                .limiter(Limiter.withRate(1, 1000))

                // disabled baseline and warm-up stages
                .settings(b -> b
                        .baseline(Settings::disabled)
                        .warmUp(Settings::disabled)
                        .test(settings -> settings
                                .threadCount(1)
                                .time(10_000)))

                // add example test suite via static init method
                .createSuite(ExampleTestApp::createUserControllerSuite, webClient)

                // add second test suite via annotations
                .testSuiteClass(AccountControllerLoadTest.class, new Class[]{WebClient.class}, webClient)

                .build()
                .run();
    }

    private static void createUserControllerSuite(TestSuiteBuilder<UserControllerLoadTest> builder, WebClient webClient) {
        builder.name("UserController")
                .instance(() -> new UserControllerLoadTest(webClient))
                .beforeAll(UserControllerLoadTest::beforeAll)
//                .beforeEach(UserControllerLoadTest::beforeEach)
                .testCase("getUsersBlock", UserControllerLoadTest::getUserByIdSync)
                .asyncTestCase("getUsersAsync", UserControllerLoadTest::getUserByIdAsync)
//                .afterEach(UserControllerLoadTest::afterEach)
                .afterAll(UserControllerLoadTest::afterAll)
        ;
    }
}
