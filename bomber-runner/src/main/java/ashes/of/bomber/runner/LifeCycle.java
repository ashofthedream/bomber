package ashes.of.bomber.runner;

import ashes.of.bomber.core.Iteration;
import ashes.of.bomber.core.State;
import ashes.of.bomber.methods.LifeCycleMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


public class LifeCycle<T>  {
    private static final Logger log = LogManager.getLogger();

    private final Map<String, TestCase<T>> testCases;
    private final Supplier<T> testSuite;
    private final List<LifeCycleMethod<T>> beforeAll;
    private final List<LifeCycleMethod<T>> beforeEach;
    private final List<LifeCycleMethod<T>> afterEach;
    private final List<LifeCycleMethod<T>> afterAll;


    public LifeCycle(Map<String, TestCase<T>> testCases,
                     Supplier<T> testSuite,
                     List<LifeCycleMethod<T>> beforeEach,
                     List<LifeCycleMethod<T>> afterEach,
                     List<LifeCycleMethod<T>> afterAll,
                     List<LifeCycleMethod<T>> beforeAll) {
        this.testCases = testCases;
        this.testSuite = testSuite;
        this.beforeAll = beforeAll;
        this.beforeEach = beforeEach;
        this.afterEach = afterEach;
        this.afterAll = afterAll;
    }


    public T testSuite() {
        return testSuite.get();
    }

    public Map<String, TestCase<T>> testCases() {
        return testCases;
    }

    public void beforeAll(State state, T object) {
        beforeAll.forEach(l -> beforeAll(state, object, l));
    }

    private void beforeAll(State state, T object, LifeCycleMethod<T> l) {
        log.trace("beforeAll");
        try {
            l.call(object);
        } catch (Throwable th) {
            log.warn("beforeAll failed", th);
        }
    }


    public void beforeEach(Iteration it, T object) {
        beforeEach.forEach(l -> beforeEach(it, object, l));
    }

    private void beforeEach(Iteration it, T object, LifeCycleMethod<T> l) {
        try {
            l.call(object);
        } catch (Throwable th) {
            log.warn("beforeEach failed. #{}", it.getNumber(), th);
        }
    }


    public void afterEach(Iteration it, T object) {
        afterEach.forEach(l -> afterEach(it, object, l));
    }

    private void afterEach(Iteration it, T object, LifeCycleMethod<T> l) {
        try {
            l.call(object);
        } catch (Throwable th) {
            log.warn("afterEach failed. #{}", it.getNumber(), th);
        }
    }


    public void afterAll(State state, T object) {
        log.trace("afterAll");
        afterAll.forEach(l -> afterAll(state, object, l));
    }

    private void afterAll(State state, T object, LifeCycleMethod<T> l) {
        try {
            l.call(object);
        } catch (Throwable th) {
            log.warn("afterAll failed", th);
        }
    }
}
