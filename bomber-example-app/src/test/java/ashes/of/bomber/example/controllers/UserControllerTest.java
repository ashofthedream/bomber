package ashes.of.bomber.example.controllers;

import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.core.Report;
import ashes.of.bomber.example.app.tests.UserControllerLoadTest;
import ashes.of.bomber.sink.histogram.HistogramSink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserControllerTest {
    private static final Logger log = LogManager.getLogger();

    @LocalServerPort
    private int port;

    @Ignore("because example app may return errors")
    @Test
    public void testApp() {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        Report report = new TestAppBuilder()
                .sink(new HistogramSink())
                .testSuiteClass(UserControllerLoadTest.class, new Class[]{WebClient.class}, webClient)
                .build()
                .run();

        assertEquals("load test has some errors", 0, report.getErrorsCount());
    }
}
