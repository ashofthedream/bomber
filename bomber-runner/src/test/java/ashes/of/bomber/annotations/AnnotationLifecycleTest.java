package ashes.of.bomber.annotations;

import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.tests.AllLifecycleMethodsTest;
import ashes.of.bomber.tests.BeforeAllAndAfterAllOnlyOnceTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AnnotationLifecycleTest {
    private static final Logger log = LogManager.getLogger();

    @Test
    public void testAllLifecycleMethods() {
        AllLifecycleMethodsTest test = new AllLifecycleMethodsTest();

        new TestAppBuilder()
                .name("testAllLifecycleMethods")
//                .sink(new Log4jSink())
                .testSuiteObject(test)
                .build()
                .start();

        assertEquals("beforeAll: 1 x thread",                   2, test.beforeAll.get());
        assertEquals("beforeEach: 10 Inv x Threads * Count",   40, test.beforeEach.get());
        assertEquals("testA: 10 Inv x Threads * Count",        20, test.testA.get());
        assertEquals("testB: 10 Inv x Threads * Count",        20, test.testB.get());
        assertEquals("beforeEach: Inv x Threads * Count",      40, test.afterEach.get());
        assertEquals("afterAll: 10 x thread",                   2, test.afterAll.get());
    }


    @Test
    public void beforeAllAndAfterAllWithOnlyOnceFlagShouldBeInvokedOnlyOnce() {
        BeforeAllAndAfterAllOnlyOnceTest test = new BeforeAllAndAfterAllOnlyOnceTest();

        new TestAppBuilder()
                .name("beforeAndAfterAllOnlyOnce")
//                .sink(new Log4jSink())
                .testSuiteObject(test)
                .build()
                .start();

        assertEquals("beforeAll: onlyOnce = true",              1, test.beforeAll.get());
        assertEquals("afterAll: onlyOnce = true",               1, test.afterAll.get());
    }
}
