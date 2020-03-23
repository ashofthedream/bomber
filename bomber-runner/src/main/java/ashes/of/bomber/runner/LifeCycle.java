package ashes.of.bomber.runner;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.State;
import ashes.of.bomber.methods.LifeCycleMethod;
import ashes.of.bomber.methods.TestCaseMethodWithClick;
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
        log.trace("{} | beforeAll", state);
        try {
            l.call(object);
        } catch (Throwable th) {
            log.warn("{} | beforeAll failed", state, th);
        }
    }


    public void beforeEach(Context context, T object) {
        beforeEach.forEach(l -> beforeEach(context, object, l));
    }

    private void beforeEach(Context context, T object, LifeCycleMethod<T> l) {
        try {
            l.call(object);
        } catch (Throwable th) {
            log.warn("{} | beforeEach failed. inv: {}",
                    context.toLogString(), context.getInv(), th);
        }
    }


    public void afterEach(Context context, T object) {
        afterEach.forEach(l -> afterEach(context, object, l));
    }

    private void afterEach(Context context, T object, LifeCycleMethod<T> l) {
        try {
            l.call(object);
        } catch (Throwable th) {
            log.warn("{} | afterEach failed. inv: {}",
                    context.toLogString(), context.getInv(), th);
        }
    }


    public void afterAll(State state, T object) {
        log.trace("{} | afterAll", state);
        afterAll.forEach(l -> afterAll(state, object, l));
    }

    private void afterAll(State state, T object, LifeCycleMethod<T> l) {
        try {
            l.call(object);
        } catch (Throwable th) {
            log.warn("{} | afterAll failed", state, th);
        }
    }
}
