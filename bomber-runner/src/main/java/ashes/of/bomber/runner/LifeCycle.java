package ashes.of.bomber.runner;

import ashes.of.bomber.core.Iteration;
import ashes.of.bomber.methods.LifeCycleMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


public class LifeCycle<T>  {
    private static final Logger log = LogManager.getLogger();

    private final Map<String, TestCase<T>> testCases;
    private final Supplier<T> instance;
    private final List<LifeCycleMethod<T>> beforeAll;
    private final List<LifeCycleMethod<T>> beforeEach;
    private final List<LifeCycleMethod<T>> afterEach;
    private final List<LifeCycleMethod<T>> afterAll;


    public LifeCycle(Map<String, TestCase<T>> testCases,
                     Supplier<T> instance,
                     List<LifeCycleMethod<T>> beforeEach,
                     List<LifeCycleMethod<T>> afterEach,
                     List<LifeCycleMethod<T>> afterAll,
                     List<LifeCycleMethod<T>> beforeAll) {
        this.testCases = testCases;
        this.instance = instance;
        this.beforeAll = beforeAll;
        this.beforeEach = beforeEach;
        this.afterEach = afterEach;
        this.afterAll = afterAll;
    }


    public T instance() {
        return instance.get();
    }

    public Map<String, TestCase<T>> testCases() {
        return testCases;
    }

    public void beforeAll(T object) {
        beforeAll.forEach(l -> beforeAll(object, l));
    }

    private void beforeAll(T object, LifeCycleMethod<T> l) {
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

    public void beforeEach(Iteration it, T object, LifeCycleMethod<T> l) {
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

    public void afterAll(T object) {
        log.trace("afterAll");
        afterAll.forEach(l -> afterAll(object, l));
    }

    private void afterAll(T object, LifeCycleMethod<T> l) {
        try {
            l.call(object);
        } catch (Throwable th) {
            log.warn("afterAll failed", th);
        }
    }
}
