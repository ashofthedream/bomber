package ashes.of.bomber.runner;

import ashes.of.bomber.core.BomberApp;
import ashes.of.bomber.tests.Counters;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public abstract class LifecycleTest {

    protected Counters counters;
    protected BomberApp app;

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
        assertEquals("init: 1 x thread",                        2, counters.init.get());

        assertEquals("beforeSuite: 1 x thread",                 2, counters.beforeSuite.get());
        assertEquals("beforeSuite(onlyOnce) ",                  1, counters.beforeSuiteOnce.get());
        assertEquals("beforeCase:  1 x thread x testCase",      4, counters.beforeCase.get());
        assertEquals("beforeCase(onlyOnce) x testCase",         2, counters.beforeCaseOnce.get());
        assertEquals("beforeEach: 10 Inv x Threads * Count",   40, counters.beforeEach.get());

        assertEquals("testA: 10 Inv x Threads * Count",        20, counters.testA.get());
        assertEquals("testB: 10 Inv x Threads * Count",        20, counters.testB.get());

        assertEquals("beforeEach: Inv x Threads * Count",      40, counters.afterEach.get());
        assertEquals("afterCase: 1 x thread x testCase",        4, counters.afterCase.get());
        assertEquals("afterCase(onlyOnce) x testCase",          2, counters.afterCaseOnce.get());
        assertEquals("afterSuite: 1 x thread",                  2, counters.afterSuite.get());
        assertEquals("afterSuite(onlyOnce)",                    1, counters.afterSuiteOnce.get());
    }
}
