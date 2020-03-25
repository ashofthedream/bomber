package ashes.of.bomber.tests;

import ashes.of.bomber.annotations.*;
import ashes.of.bomber.stopwatch.Clock;
import ashes.of.bomber.stopwatch.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

@LoadTestSuite(name = "lifecycleAll")
@LoadTest(time = 20, threadIterations = 10, threads = 2)
public class AllLifecycleMethodsTest {
    private static final Logger log = LogManager.getLogger();

    public final AtomicInteger beforeAll = new AtomicInteger();
    public final AtomicInteger beforeEach = new AtomicInteger();
    public final AtomicInteger testA = new AtomicInteger();
    public final AtomicInteger testB = new AtomicInteger();
    public final AtomicInteger afterEach = new AtomicInteger();
    public final AtomicInteger afterAll = new AtomicInteger();


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
        log.trace("This method will be invoked before each test invocation");
        beforeEach.incrementAndGet();
    }

    @AfterEach
    public void afterEach() {
        log.trace("This method will be invoked after each test invocation");
        afterEach.incrementAndGet();
    }

    @LoadTestCase
    public void testA() {
        log.trace("testA");
        testA.incrementAndGet();
    }

    @LoadTestCase
    public void testB(Clock clock) {
        Stopwatch stopwatch = clock.stopwatch("testB-stopwatch-1");
        log.trace("testB");
        stopwatch.success();
        testB.incrementAndGet();
    }
}
