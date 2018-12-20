package ashes.of.loadtest.builder;

import ashes.of.loadtest.sink.Log4jSink;
import ashes.of.loadtest.stopwatch.Lap;
import ashes.of.loadtest.stopwatch.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;


public class TestCaseBuilderTest {
    private static final Logger log = LogManager.getLogger(TestCaseBuilderTest.class);


    public static class AllLifecycleMethodsTest {

        private final AtomicInteger beforeAll = new AtomicInteger();
        private final AtomicInteger beforeEach = new AtomicInteger();
        private final AtomicInteger testA = new AtomicInteger();
        private final AtomicInteger testB = new AtomicInteger();
        private final AtomicInteger afterEach = new AtomicInteger();
        private final AtomicInteger afterAll = new AtomicInteger();

        public void beforeAll() {
            log.info("This method will be invoked before all the tests in each thread");
            beforeAll.incrementAndGet();
        }

        public void afterAll() {
            log.debug("This method will be invoked after all the tests in each thread");
            afterAll.incrementAndGet();
        }

        public void beforeEach() {
            log.debug("This method will be invoked before each test invocation");
            beforeEach.incrementAndGet();
        }

        public void afterEach() {
            log.debug("This method will be invoked after each test invocation");
            afterEach.incrementAndGet();
        }

        public void testA() {
            log.debug("testA");
            testA.incrementAndGet();
        }

        public void testB(Stopwatch stopwatch) {
            Lap lap = stopwatch.lap("testB-lap-1");
            log.debug("testB");
            lap.elapsed();
            testB.incrementAndGet();
        }
    }


    @Test
    public void testAllLifecycleMethods() {
        AllLifecycleMethodsTest test = new AllLifecycleMethodsTest();

        new TestCaseBuilder<AllLifecycleMethodsTest>()
                .name("testAllLifecycleMethods")
                .testCase(test)
                .settings(s -> s
                        .baseline(Settings::disabled)
                        .warmUp(Settings::disabled)
                        .test(settings -> settings
                                .time(20_000)
                                .threadCount(2)
                                .threadInvocationCount(10)))
                .sink(new Log4jSink())
                .beforeAll(AllLifecycleMethodsTest::beforeAll)
                .beforeEach(AllLifecycleMethodsTest::beforeEach)
                .test("testA", AllLifecycleMethodsTest::testA)
                .test("testB", AllLifecycleMethodsTest::testB)
                .afterEach(AllLifecycleMethodsTest::afterEach)
                .afterAll(AllLifecycleMethodsTest::afterAll)
                .build()
                .run();

        assertEquals("beforeAll: 1 x thread",                   2, test.beforeAll.get());
        assertEquals("beforeEach: 10 Inv x Threads * Count",   40, test.beforeEach.get());
        assertEquals("testA: 10 Inv x Threads * Count",        20, test.testA.get());
        assertEquals("testB: 10 Inv x Threads * Count",        20, test.testB.get());
        assertEquals("beforeEach: Inv x Threads * Count",      40, test.afterEach.get());
        assertEquals("afterAll: 10 x thread",                   2, test.afterAll.get());
    }



    public static class BeforeAllAndAfterAllOnlyOnceTest {

        private final AtomicInteger beforeAll = new AtomicInteger();
        private final AtomicInteger afterAll = new AtomicInteger();

        public void beforeAll() {
            log.info("This method will be invoked before all the tests only once");
            beforeAll.incrementAndGet();
        }

        public void afterAll() {
            log.info("This method will be invoked after all the tests only once");
            afterAll.incrementAndGet();
        }

        public void test() {
            log.debug("test");
        }
    }


    @Test
    public void beforeAllWithOnlyOnceShouldBeInvokedOnlyOnce() {
        BeforeAllAndAfterAllOnlyOnceTest test = new BeforeAllAndAfterAllOnlyOnceTest();

        new TestCaseBuilder<BeforeAllAndAfterAllOnlyOnceTest>()
                .name("beforeAllWithOnlyOnceShouldBeInvokedOnlyOnce")
                .testCase(test)
                .settings(b -> b
                        .baseline(Settings::disabled)
                        .warmUp(Settings::disabled)
                        .test(settings -> settings
                                .time(20_000)
                                .threadCount(2)
                                .threadInvocationCount(10)))
                .sink(new Log4jSink())
                .beforeAll(true, BeforeAllAndAfterAllOnlyOnceTest::beforeAll)
                .test("test", BeforeAllAndAfterAllOnlyOnceTest::test)
                .build()
                .run();

        assertEquals("beforeAll: onlyOnce = true",              1, test.beforeAll.get());
    }


    @Test
    public void afterAllWithOnlyOnceShouldBeInvokedOnlyOnce() {
        BeforeAllAndAfterAllOnlyOnceTest test = new BeforeAllAndAfterAllOnlyOnceTest();




        new TestCaseBuilder<BeforeAllAndAfterAllOnlyOnceTest>()
                .name("afterAllWithOnlyOnceShouldBeInvokedOnlyOnce")
                .testCase(test)
                .settings(b -> b
                        .baseline(Settings::disabled)
                        .warmUp(Settings::disabled)
                        .test(settings -> settings
                                .time(20_000)
                                .threadCount(2)
                                .threadInvocationCount(10)))
                .sink(new Log4jSink())
                .test("test", BeforeAllAndAfterAllOnlyOnceTest::test)
                .afterAll(true, BeforeAllAndAfterAllOnlyOnceTest::afterAll)
                .build()
                .run();

        assertEquals("afterAll: onlyOnce = true",              1, test.afterAll.get());
    }
}
