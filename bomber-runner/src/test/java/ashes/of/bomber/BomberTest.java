package ashes.of.bomber;

import ashes.of.bomber.annotations.LoadTestApp;
import ashes.of.bomber.annotations.LoadTestCase;
import ashes.of.bomber.annotations.LoadTestSettings;
import ashes.of.bomber.annotations.LoadTestSuite;
import ashes.of.bomber.builder.BomberBuilder;
import ashes.of.bomber.events.TestCaseAfterEachEvent;
import ashes.of.bomber.sink.Sink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BomberTest {
    private static final Logger log = LogManager.getLogger();

    @LoadTestApp
    @LoadTestSettings(time = 1)
    @LoadTestSuite
    public static class DummyLoadTest {

        @LoadTestCase
        public void dummy() throws InterruptedException {
            Thread.sleep(100);
        }
    }


    @Test
    public void addSinkInRuntime() {
        var counter = new AtomicInteger();

        var bomber = new BomberBuilder()
                .add(DummyLoadTest.class)
                .build();

        var f = bomber.startAsync();

        bomber.addSink(new Sink() {
            @Override
            public void afterEach(TestCaseAfterEachEvent event) {
                counter.incrementAndGet();
            }
        });

        f.join();

        assertTrue(counter.get() > 0);
    }

    @Test
    public void addWatcherInRuntime() {
        var counter = new AtomicInteger();

        var bomber = new BomberBuilder()
                .add(DummyLoadTest.class)
                .build();

        var f = bomber.startAsync();

        bomber.addWatcher(event -> counter.incrementAndGet());

        f.join();

        assertTrue(counter.get() > 0);
    }
}