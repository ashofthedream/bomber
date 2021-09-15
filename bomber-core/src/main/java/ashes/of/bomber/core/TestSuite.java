package ashes.of.bomber.core;

import ashes.of.bomber.flight.Iteration;
import ashes.of.bomber.methods.LifeCycleHolder;
import ashes.of.bomber.methods.LifeCycleMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TestSuite<T> {
    private static final Logger log = LogManager.getLogger();

    private final String name;
    private final Supplier<T> context;
    private final List<LifeCycleHolder<T>> beforeSuite;
    private final List<LifeCycleHolder<T>> beforeCase;
    private final List<LifeCycleHolder<T>> beforeEach;
    private final Map<String, TestCase<T>> testCases;
    private final List<LifeCycleHolder<T>> afterEach;
    private final List<LifeCycleHolder<T>> afterCase;
    private final List<LifeCycleHolder<T>> afterSuite;

    public TestSuite(String name,
                     Supplier<T> context,
                     List<LifeCycleHolder<T>> beforeSuite,
                     List<LifeCycleHolder<T>> beforeCase,
                     List<LifeCycleHolder<T>> beforeEach,
                     Map<String, TestCase<T>> testCases,
                     List<LifeCycleHolder<T>> afterEach,
                     List<LifeCycleHolder<T>> afterCase,
                     List<LifeCycleHolder<T>> afterSuite) {
        this.name = name;
        this.context = context;
        this.beforeSuite = beforeSuite;
        this.beforeCase = beforeCase;
        this.beforeEach = beforeEach;
        this.testCases = testCases;
        this.afterEach = afterEach;
        this.afterCase = afterCase;
        this.afterSuite = afterSuite;
    }

    public String getName() {
        return name;
    }

    public Object getContext() {
        return context.get();
    }

    public Collection<TestCase<T>> getTestCases() {
        return testCases.values();
    }

    public TestCase<T> getTestCase(String name) {
        var testCase = testCases.get(name);
        if (testCase == null)
            throw new IllegalArgumentException("Test suite " + this.name + " doesn't contain test case " + name);

        return testCase;
    }

    public void resetBeforeAndAfterSuite() {
        Stream.concat(beforeSuite.stream(), afterSuite.stream())
                .forEach(LifeCycleHolder::reset);
    }

    public void resetBeforeAndAfterCase() {
        Stream.concat(beforeCase.stream(), afterCase.stream())
                .forEach(LifeCycleHolder::reset);
    }


    public void beforeSuite(T object) {
        beforeSuite.forEach(l -> beforeSuite(object, l));
    }

    private void beforeSuite(T object, LifeCycleMethod<T> l) {
        log.trace("Call beforeSuite instance: {}", object);
        try {
            l.call(object);
        } catch (Throwable th) {
            log.warn("Call beforeSuite instance: {}, failed", object, th);
        }
    }


    public void beforeCase(T object) {
        beforeCase.forEach(l -> beforeCase(object, l));
    }

    private void beforeCase(T object, LifeCycleMethod<T> l) {
        try {
            log.trace("Call beforeCase instance: {}", object);
            l.call(object);
        } catch (Throwable th) {
            log.warn("Call beforeCase instance: {}, failed", object, th);
        }
    }    


    public void beforeEach(Iteration it, T object) {
        beforeEach.forEach(method -> beforeEach(it, object, method));
    }

    private void beforeEach(Iteration it, T object, LifeCycleMethod<T> method) {
        try {
            log.trace("Call beforeEach instance: {}, it: {}", object, it);
            method.call(object);
        } catch (Throwable th) {
            log.trace("Call beforeEach instance: {}, it: {} failed", object, it, th);
        }
    }


    public void afterEach(Iteration it, T object) {
        afterEach.forEach(l -> afterEach(it, object, l));
    }

    private void afterEach(Iteration it, T object, LifeCycleMethod<T> method) {
        try {
            log.trace("Call afterEach instance: {}, it: {}", object, it);
            method.call(object);
        } catch (Throwable th) {
            log.trace("Call afterEach instance: {}, it: {} failed", object, it, th);
        }
    }


    public void afterCase(T object) {
        afterCase.forEach(method -> afterCase(object, method));
    }

    private void afterCase(T object, LifeCycleMethod<T> method) {
        try {
            log.trace("Call afterCase instance: {}", object);
            method.call(object);
        } catch (Throwable th) {
            log.warn("Call afterCase instance: {}, failed", object, th);
        }
    }


    public void afterSuite(T object) {
        afterSuite.forEach(method -> afterSuite(object, method));
    }

    private void afterSuite(T object, LifeCycleMethod<T> method) {
        try {
            log.trace("Call afterSuite instance: {}", object);
            method.call(object);
        } catch (Throwable th) {
            log.warn("Call afterSuite instance: {}, failed", object, th);
        }
    }
}
