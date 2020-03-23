package ashes.of.bomber.tests;

import ashes.of.bomber.annotations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

@LoadTestSuite(name = "onlyOnceBeforeAndAfterAll")
@LoadTest(time = 20, threadInvocations = 10, threads = 2)
public class BeforeAllAndAfterAllOnlyOnceTest {
    private static final Logger log = LogManager.getLogger();

    public final AtomicInteger beforeAll = new AtomicInteger();
    public final AtomicInteger afterAll = new AtomicInteger();

    @BeforeAll(onlyOnce = true)
    public void beforeAll() {
        log.debug("This method will be invoked before all the tests only once");
        beforeAll.incrementAndGet();
    }

    @AfterAll(onlyOnce = true)
    public void afterAll() {
        log.debug("This method will be invoked after all the tests only once");
        afterAll.incrementAndGet();
    }

    @LoadTestCase
    public void test() {
        log.trace("test");
    }
}
