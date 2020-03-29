package ashes.of.bomber.builder;

import ashes.of.bomber.core.Settings;
import ashes.of.bomber.tests.AllLifecycleMethodsTest;
import ashes.of.bomber.tests.BeforeAllAndAfterAllOnlyOnceTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class BuilderLifecycleTest {
    private static final Logger log = LogManager.getLogger(BuilderLifecycleTest.class);


    @Test
    public void testAllLifecycleMethods() {
        AllLifecycleMethodsTest test = new AllLifecycleMethodsTest();

        TestSuiteBuilder<AllLifecycleMethodsTest> suite = new TestSuiteBuilder<AllLifecycleMethodsTest>()
                .name("lifecycleAll")
                .sharedInstance(test)
                .warmUp(this::warmUpSettings)
                .settings(this::testSettings)
                .beforeAll(AllLifecycleMethodsTest::beforeAll)
                .beforeEach(AllLifecycleMethodsTest::beforeEach)
                .testCase("testA", AllLifecycleMethodsTest::testA)
                .testCase("testB", AllLifecycleMethodsTest::testB)
                .afterEach(AllLifecycleMethodsTest::afterEach)
                .afterAll(AllLifecycleMethodsTest::afterAll);

        new TestAppBuilder()
                .name("testAllLifecycleMethods")
//                .sink(new Log4jSink())
                .addSuite(suite)
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

        TestSuiteBuilder<BeforeAllAndAfterAllOnlyOnceTest> suite = new TestSuiteBuilder<BeforeAllAndAfterAllOnlyOnceTest>()
                .name("onlyOnceBeforeAndAfterAll")
                .sharedInstance(test)
                .warmUp(this::warmUpSettings)
                .settings(this::testSettings)
                .beforeAll(true, BeforeAllAndAfterAllOnlyOnceTest::beforeAll)
                .testCase("test", BeforeAllAndAfterAllOnlyOnceTest::test)
                .afterAll(true, BeforeAllAndAfterAllOnlyOnceTest::afterAll);

        new TestAppBuilder()
                .name("beforeAndAfterAllOnlyOnce")
//                .sink(new Log4jSink())
                .addSuite(suite)
                .build()
                .start();

        assertEquals("beforeAll: onlyOnce = true",              1, test.beforeAll.get());
        assertEquals("afterAll: onlyOnce = true",               1, test.afterAll.get());
    }

    private void warmUpSettings(Settings settings) {
        settings.disabled();
    }

    private void testSettings(Settings settings) {
        settings.seconds(20)
                .threadCount(2)
                .threadIterations(10);
    }
}
