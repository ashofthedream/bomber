package ashes.of.bomber.runner;

import ashes.of.bomber.core.Iteration;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.methods.LifeCycleHolder;
import ashes.of.bomber.methods.LifeCycleMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TestSuite<T> {
    private static final Logger log = LogManager.getLogger();

    private final String name;
    private final Environment env;
    private final Supplier<T> instance;
    private final List<LifeCycleHolder<T>> beforeSuite;
    private final List<LifeCycleHolder<T>> beforeCase;
    private final List<LifeCycleHolder<T>> beforeEach;
    private final Map<String, TestCase<T>> testCases;
    private final List<LifeCycleHolder<T>> afterEach;
    private final List<LifeCycleHolder<T>> afterCase;
    private final List<LifeCycleHolder<T>> afterSuite;
    private final Settings settings;
    private final Settings warmUp;


    public TestSuite(String name, Environment env,
                     Supplier<T> instance,
                     List<LifeCycleHolder<T>> beforeSuite,
                     List<LifeCycleHolder<T>> beforeCase,
                     List<LifeCycleHolder<T>> beforeEach,
                     Map<String, TestCase<T>> testCases,
                     List<LifeCycleHolder<T>> afterEach,
                     List<LifeCycleHolder<T>> afterCase,
                     List<LifeCycleHolder<T>> afterSuite,
                     Settings settings, Settings warmUp) {
        this.name = name;
        this.env = env;
        this.instance = instance;
        this.beforeSuite = beforeSuite;
        this.beforeCase = beforeCase;
        this.beforeEach = beforeEach;
        this.testCases = testCases;
        this.afterEach = afterEach;
        this.afterCase = afterCase;
        this.afterSuite = afterSuite;
        this.settings = new Settings(settings);
        this.warmUp = new Settings(warmUp);
    }


    public String getName() {
        return name;
    }

    public Environment getEnv() {
        return env;
    }

    public Object instance() {
        return instance.get();
    }

    public Map<String, TestCase<T>> testCases() {
        return testCases;
    }

    public TestCase<T> getTestCase(String testCase) {
        return testCases.computeIfAbsent(testCase,
                k -> { throw new IllegalArgumentException("Test suite " + name + " doesn't contain test case " + k); });
    }

    public void resetBeforeAndAfterSuite() {
        Stream.concat(beforeSuite.stream(), afterSuite.stream())
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


    public void resetBeforeAndAfterCase() {
        Stream.concat(beforeCase.stream(), afterCase.stream())
                .forEach(LifeCycleHolder::reset);
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

    public Settings getWarmUp() {
        return warmUp;
    }

    public Settings getSettings() {
        return settings;
    }

    public Collection<TestCase<T>> getTestCases() {
        return testCases.values();
    }
}
