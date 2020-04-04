package ashes.of.bomber.tests;

import ashes.of.bomber.annotations.*;
import ashes.of.bomber.stopwatch.Stopwatch;
import ashes.of.bomber.stopwatch.Tools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@LoadTestSuite(name = "lifecycleAll")
@LoadTest(time = 20, threadIterations = 10, threads = 2)
public class AllLifecycleMethodsTest {
    private static final Logger log = LogManager.getLogger();

    public final Counters counters;

    public AllLifecycleMethodsTest(Counters counters) {
        this.counters = counters;
        this.counters.init.incrementAndGet();
    }

    @BeforeTestSuite
    public void beforeSuite() {
        log.info("beforeSuite should be invoked before all the tests in each thread");
        counters.beforeSuite.incrementAndGet();
    }

    @BeforeTestSuite(onlyOnce = true)
    public void beforeSuiteOnlyOnce() {
        log.info("beforeSuiteOnlyOnce should be invoked before all the tests only once");
        counters.beforeSuiteOnce.incrementAndGet();
    }

    @BeforeTestCase
    public void beforeCase() {
        log.info("beforeCase should be invoked once before test case in each thread");
        counters.beforeCase.incrementAndGet();
    }

    @BeforeTestCase(onlyOnce = true)
    public void beforeCaseOnlyOnce() {
        log.info("beforeCaseOnlyOnce should be invoked before test case only once");
        counters.beforeCaseOnce.incrementAndGet();
    }

    @BeforeEach
    public void beforeEach() {
        log.trace("beforeEach should be invoked before each test invocation");
        counters.beforeEach.incrementAndGet();
    }

    @LoadTestCase
    public void testA() {
        log.trace("testA");
        counters.testA.incrementAndGet();
    }

    @LoadTestCase(async = true)
    public void testB(Tools tools) {
        log.trace("testB");
        Stopwatch stopwatch = tools.stopwatch("testB");
        stopwatch.success();
        counters.testB.incrementAndGet();
    }

    @AfterEach
    public void afterEach() {
        log.trace("afterEach should be invoked after each test invocation");
        counters.afterEach.incrementAndGet();
    }

    @AfterTestCase
    public void afterCase() {
        log.info("afterCase should be invoked after all test case in each thread");
        counters.afterCase.incrementAndGet();
    }

    @AfterTestCase(onlyOnce = true)
    public void afterCaseOnlyOnce() {
        log.info("afterCaseOnlyOnce should be invoked after test case only once");
        counters.afterCaseOnce.incrementAndGet();
    }

    @AfterTestSuite
    public void afterSuite() {
        log.info("afterSuite should be invoked after all the tests in each thread");
        counters.afterSuite.incrementAndGet();
    }

    @AfterTestSuite(onlyOnce = true)
    public void afterSuiteOnlyOnce() {
        log.info("afterSuiteOnlyOnce should be invoked after all the tests only once");
        counters.afterSuiteOnce.incrementAndGet();
    }
}
