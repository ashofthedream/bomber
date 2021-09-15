package ashes.of.bomber.runner;

import ashes.of.bomber.core.TestApp;
import ashes.of.bomber.runner.tests.Counters;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public abstract class LifecycleTest {

    protected Counters counters;
    protected TestApp app;

    @Test
    public void testAllLifecycleMethods() {
        app.start();
        assertCounters(counters);
    }

    @Test
    public void testAllLifecycleMethodsWhenAppRunsTwice() {
        app.start();
        assertCounters(counters);

        counters.reset();
        app.start();
        assertCounters(counters);
    }

    protected void assertCounters(Counters counters) {
        // init: 1 x thread
        assertEquals(2, counters.init.get());

        // beforeSuite: 1 x thread
        assertEquals(2, counters.beforeSuite.get());

        // beforeSuite(onlyOnce) 
        assertEquals(1, counters.beforeSuiteOnce.get());

        // beforeCase:  1 x thread x testCase
        assertEquals(4, counters.beforeCase.get());

        // beforeCase(onlyOnce) x testCase
        assertEquals(2, counters.beforeCaseOnce.get());

        // beforeEach: 10 Inv x Threads * Count
        assertEquals(40, counters.beforeEach.get());

        // testA: 10 Inv x Threads * Count
        assertEquals(20, counters.testA.get());

        // testB: 10 Inv x Threads * Count
        assertEquals(20, counters.testB.get());

        // beforeEach: Inv x Threads * Count
        assertEquals(40, counters.afterEach.get());

        // afterCase: 1 x thread x testCase
        assertEquals(4, counters.afterCase.get());

        // afterCase(onlyOnce) x testCase
        assertEquals(2, counters.afterCaseOnce.get());

        // afterSuite: 1 x thread
        assertEquals(2, counters.afterSuite.get());

        // afterSuite(onlyOnce)
        assertEquals(1, counters.afterSuiteOnce.get());
    }
}
