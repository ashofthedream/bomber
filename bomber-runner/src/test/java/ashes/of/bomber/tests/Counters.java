package ashes.of.bomber.tests;

import java.util.concurrent.atomic.AtomicInteger;

public class Counters {
    public final AtomicInteger init = new AtomicInteger();
    public final AtomicInteger beforeSuite = new AtomicInteger();
    public final AtomicInteger beforeSuiteOnce = new AtomicInteger();
    public final AtomicInteger beforeCase = new AtomicInteger();
    public final AtomicInteger beforeCaseOnce = new AtomicInteger();
    public final AtomicInteger beforeEach = new AtomicInteger();
    public final AtomicInteger testA = new AtomicInteger();
    public final AtomicInteger testB = new AtomicInteger();
    public final AtomicInteger afterEach = new AtomicInteger();
    public final AtomicInteger afterCase = new AtomicInteger();
    public final AtomicInteger afterCaseOnce = new AtomicInteger();
    public final AtomicInteger afterSuite = new AtomicInteger();
    public final AtomicInteger afterSuiteOnce = new AtomicInteger();

    public void reset() {
        init.set(0);
        beforeSuite.set(0);
        beforeSuiteOnce.set(0);
        beforeCase.set(0);
        beforeCaseOnce.set(0);
        beforeEach.set(0);
        testA.set(0);
        testB.set(0);
        afterEach.set(0);
        afterCase.set(0);
        afterCaseOnce.set(0);
        afterSuite.set(0);
        afterSuiteOnce.set(0);
    }
}
