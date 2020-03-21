package ashes.of.bomber.runner;

import ashes.of.bomber.core.Context;
import ashes.of.bomber.core.State;
import ashes.of.bomber.methods.LifeCycleMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


public class LifeCycle<T>  {
    private static final Logger log = LogManager.getLogger();

    private final List<LifeCycleMethod<T>> beforeAll;
    private final List<LifeCycleMethod<T>> beforeEach;
    private final List<LifeCycleMethod<T>> afterEach;
    private final List<LifeCycleMethod<T>> afterAll;

    public LifeCycle(List<LifeCycleMethod<T>> beforeAll,
                     List<LifeCycleMethod<T>> beforeEach,
                     List<LifeCycleMethod<T>> afterEach,
                     List<LifeCycleMethod<T>> afterAll) {
        this.beforeAll = beforeAll;
        this.beforeEach = beforeEach;
        this.afterEach = afterEach;
        this.afterAll = afterAll;
    }


    public void beforeAll(State state, T testCase) {
        beforeAll.forEach(l -> beforeAll(state, testCase, l));
    }

    private void beforeAll(State state, T testCase, LifeCycleMethod<T> l) {
        log.trace("beforeAll stage: {}, testCase: {}", state.getStage(), state.getTestCase());
        try {
            l.call(testCase);
        } catch (Throwable th) {
            log.warn("beforeAll failed. stage: {}, testCase: {}",
                    state.getStage(), state.getTestCase(), th);
        }
    }


    public void beforeEach(Context context, T testCase) {
        beforeEach.forEach(l -> beforeEach(context, testCase, l));
    }

    private void beforeEach(Context context, T testCase, LifeCycleMethod<T> l) {
        try {
            l.call(testCase);
        } catch (Throwable th) {
            log.warn("beforeEach failed. stage: {}, testCase: {}, test: {}, inv: {}",
                    context.getStage(), context.getTestCase(), context.getTest(), context.getInv(), th);
        }
    }


    public void afterEach(Context context, T testCase) {
        afterEach.forEach(l -> afterEach(context, testCase, l));
    }

    private void afterEach(Context context, T testCase, LifeCycleMethod<T> l) {
        try {
            l.call(testCase);
        } catch (Throwable th) {
            log.warn("afterEach failed. stage: {}, testCase: {}, test: {}, inv: {}",
                    context.getStage(), context.getTestCase(), context.getTest(), context.getInv(), th);
        }
    }


    public void afterAll(State state, T testCase) {
        log.trace("afterAll stage: {}, testCase: {}", state.getStage(), state.getTestCase());
        afterAll.forEach(l -> afterAll(state, testCase, l));
    }

    private void afterAll(State state, T testCase, LifeCycleMethod<T> l) {
        try {
            l.call(testCase);
        } catch (Throwable th) {
            log.warn("afterAll failed. stage: {}, testCase: {}",
                    state.getStage(), state.getTestCase(), th);
        }
    }
}
