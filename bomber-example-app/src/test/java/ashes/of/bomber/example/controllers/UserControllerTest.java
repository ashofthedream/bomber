package ashes.of.bomber.example.controllers;

import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.flight.TestFlightReport;
import ashes.of.bomber.example.app.tests.UserControllerLoadTest;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.sink.histogram.HistogramSink;
import ashes.of.bomber.tools.Record;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.atomic.LongAdder;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.util.AssertionErrors.assertEquals;

//@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserControllerTest {
    private static final Logger log = LogManager.getLogger();

    private static class ErrorCounter implements Sink {

        private final LongAdder iterations = new LongAdder();
        private final LongAdder errors = new LongAdder();

        @Override
        public void timeRecorded(Record record) {
            iterations.increment();
            if (record.getError() != null)
                errors.increment();
        }

        public long getErrorsCount() {
            return errors.sum();
        }
    }

    @LocalServerPort
    private int port;

    @Disabled("because example app may return errors")
    @Test
    public void testApp() {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        ErrorCounter errorCounter = new ErrorCounter();
        TestFlightReport report = new TestAppBuilder()
                .sink(new HistogramSink())
                .sink(errorCounter)
                .testSuiteClass(UserControllerLoadTest.class, new Class[]{WebClient.class}, webClient)
                .build()
                .start();

        assertEquals("load test has some errors", 0, errorCounter.getErrorsCount());
    }
}
