package ashes.of.bomber.annotations;

import ashes.of.bomber.builder.TestSuiteBuilder;
import ashes.of.bomber.sink.Log4jSink;
import ashes.of.bomber.core.stopwatch.Lap;
import ashes.of.bomber.core.stopwatch.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;


public class LifecycleTest {
    private static final Logger log = LogManager.getLogger(LifecycleTest.class);


    @LoadTestCase(time = 20, threadInvocations = 10, threads = 2)
    @Warmup(disabled = true)
    @Baseline(disabled = true)
    public static class AllLifecycleMethodsTest {

        private final AtomicInteger beforeAll = new AtomicInteger();
        private final AtomicInteger beforeEach = new AtomicInteger();
        private final AtomicInteger testA = new AtomicInteger();
        private final AtomicInteger testB = new AtomicInteger();
        private final AtomicInteger afterEach = new AtomicInteger();
        private final AtomicInteger afterAll = new AtomicInteger();


        @BeforeAll
        public void beforeAll() {
            log.info("This method will be invoked before all the tests in each thread");
            beforeAll.incrementAndGet();
        }

        @AfterAll
        public void afterAll() {
            log.debug("This method will be invoked after all the tests in each thread");
            afterAll.incrementAndGet();
        }

        @BeforeEach
        public void beforeEach() {
            log.debug("This method will be invoked before each test invocation");
            beforeEach.incrementAndGet();
        }

        @AfterEach
        public void afterEach() {
            log.debug("This method will be invoked after each test invocation");
            afterEach.incrementAndGet();
        }

        @LoadTest
        public void testA() {
            log.debug("testA");
            testA.incrementAndGet();
        }

        @LoadTest
        public void testB(Stopwatch stopwatch) {
            Lap lap = stopwatch.lap("testB-lap-1");
            log.debug("testB");
            lap.record();
            testB.incrementAndGet();
        }
    }


    @Test
    public void testAllLifecycleMethods() {
        AllLifecycleMethodsTest test = new AllLifecycleMethodsTest();

        new TestSuiteBuilder()
                .sink(new Log4jSink())
                .addInstance(test)
                .build()
                .run();


        assertEquals("beforeAll: 1 x thread",                   2, test.beforeAll.get());
        assertEquals("beforeEach: 10 Inv x Threads * Count",   40, test.beforeEach.get());
        assertEquals("testA: 10 Inv x Threads * Count",        20, test.testA.get());
        assertEquals("testB: 10 Inv x Threads * Count",        20, test.testB.get());
        assertEquals("beforeEach: Inv x Threads * Count",      40, test.afterEach.get());
        assertEquals("afterAll: 10 x thread",                   2, test.afterAll.get());
    }



    @LoadTestCase(time = 20, threadInvocations = 10, threads = 2)
    @Warmup(disabled = true)
    @Baseline(disabled = true)
    public static class BeforeAllAndAfterAllOnlyOnceTest {

        private final AtomicInteger beforeAll = new AtomicInteger();
        private final AtomicInteger afterAll = new AtomicInteger();

        @BeforeAll(onlyOnce = true)
        public void beforeAll() {
            log.info("This method will be invoked before all the tests only once");
            beforeAll.incrementAndGet();
        }

        @AfterAll(onlyOnce = true)
        public void afterAll() {
            log.info("This method will be invoked after all the tests only once");
            afterAll.incrementAndGet();
        }

        @LoadTest
        public void test() {
            log.debug("test");
        }
    }


    @Test
    public void beforeAllWithOnlyOnceFlagShouldBeInvokedOnlyOnce() {
        BeforeAllAndAfterAllOnlyOnceTest test = new BeforeAllAndAfterAllOnlyOnceTest();

        new TestSuiteBuilder()
                .sink(new Log4jSink())
                .addInstance(test)
                .build()
                .run();

        assertEquals("beforeAll: onlyOnce = true",              1, test.beforeAll.get());
    }


    @Test
    public void afterAllWithOnlyOnceFlagShouldBeInvokedOnlyOnce() {
        BeforeAllAndAfterAllOnlyOnceTest test = new BeforeAllAndAfterAllOnlyOnceTest();

        new TestSuiteBuilder()
                .sink(new Log4jSink())
                .addInstance(test)
                .build()
                .run();

        assertEquals("afterAll: onlyOnce = true",              1, test.afterAll.get());
    }
}
