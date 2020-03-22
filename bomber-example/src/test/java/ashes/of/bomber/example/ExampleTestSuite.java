package ashes.of.bomber.example;

import ashes.of.bomber.builder.TestSuiteBuilder;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.core.limiter.Limiter;
import ashes.of.bomber.example.controllers.UserControllerLoadTest;
import ashes.of.bomber.sink.histo.HistogramTimelineSink;
import org.springframework.web.reactive.function.client.WebClient;

public class ExampleTestSuite {

    public static void main(String... args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        new TestSuiteBuilder<UserControllerLoadTest>()
                .app(app -> app.sink(new HistogramTimelineSink()))
                .name("UserController")
                .settings(b -> b
                        .warmUp(Settings::disabled)
                        .test(settings -> settings
                                .threadCount(1)
                                .time(10_000)) )

                .limiter(Limiter.withRate(1, 1000))
                .instance(() -> new UserControllerLoadTest(webClient))
                .beforeAll(UserControllerLoadTest::beforeAll)
//                .beforeEach(UserControllerLoadTest::beforeEach)
                .testCase("getUsersBlock", UserControllerLoadTest::getUserByIdSync)
                .testCase("getUsersAsync", UserControllerLoadTest::getUserByIdAsync)
//                .afterEach(UserControllerLoadTest::afterEach)
//                .afterAll(UserControllerLoadTest::afterAll)
                .application()
                .run();
    }
}
