package ashes.of.bomber.example.app;

import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.builder.TestSuiteBuilder;
import ashes.of.bomber.example.app.tests.AccountControllerLoadTest;
import ashes.of.bomber.example.app.tests.UserControllerLoadTest;
import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.sink.histogram.HistogramSink;
import ashes.of.bomber.sink.histogram.HistogramTimelinePrintStreamPrinter;
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
 * Example test application with some test cases built via builder
 */
public class ExampleBuilderTestApp {
    private static final Logger log = LogManager.getLogger();

    public static void main(String... args) {
        String url = args.length > 0 ? args[0] : "http://localhost:8080";
        int members = args.length > 1 ? Integer.parseInt(args[1]) : 1;

        WebClient webClient = WebClient.builder()
                .baseUrl(url)
                .build();

        BarrierBuilder barrier = members > 1 ? new ZookeeperBarrierBuilder().members(members) : new NoBarrier.Builder();

        new TestAppBuilder()
                .name("example")
                .settings(settings -> settings.threadCount(2).seconds(3))
                .limiter(() -> Limiter.withRate(10, 1))
                // log all times to console via log4j and HdrHistogram
//                .sink(new Log4jSink())
                .sink(new HistogramTimelineSink(ChronoUnit.SECONDS, new HistogramTimelinePrintStreamPrinter()))
//                .sink(new HistogramSink())
                .watcher(1000, new Log4jWatcher())
                .barrier(barrier)

                // add example test suite via static init method
                .createSuite(ExampleBuilderTestApp::createUserControllerSuite, webClient)
                .createSuite(ExampleBuilderTestApp::createAccountControllerSuite, webClient)
                .build()
                .start();
    }

    private static void createUserControllerSuite(TestSuiteBuilder<UserControllerLoadTest> builder, WebClient webClient) {
        builder.name("UserController")
//                .limiter(Limiter.withRate(10, 1000))
                .instance(() -> new UserControllerLoadTest(webClient))
                .beforeSuite(UserControllerLoadTest::beforeAll)
//                .beforeEach(UserControllerLoadTest::beforeEach)
                .testCase("getUsersBlock", UserControllerLoadTest::getUserByIdSync)
                .asyncTestCase("getUsersAsync", UserControllerLoadTest::getUserByIdAsync)
//                .afterEach(UserControllerLoadTest::afterEach)
                .afterSuite(UserControllerLoadTest::afterAll);
    }

    private static void createAccountControllerSuite(TestSuiteBuilder<AccountControllerLoadTest> builder, WebClient webClient) {
        builder.name("AccountController")
                .instance(() -> new AccountControllerLoadTest(webClient))
                .beforeSuite(AccountControllerLoadTest::beforeAll)
//                .beforeEach(UserControllerLoadTest::beforeEach)
                .testCase("getUsersBlock", AccountControllerLoadTest::getAccountByIdSync)
                .asyncTestCase("getUsersAsync", AccountControllerLoadTest::getAccountByIdAsync)
//                .afterEach(UserControllerLoadTest::afterEach)
                .afterSuite(AccountControllerLoadTest::afterAll);
    }
}
