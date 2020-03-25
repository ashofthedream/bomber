package ashes.of.bomber.builder;

import ashes.of.bomber.annotations.*;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.delayer.RandomDelayer;
import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.methods.TestCaseMethodWithClick;
import ashes.of.bomber.stopwatch.Clock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TestSuiteProcessor<T> {
    private static final Logger log = LogManager.getLogger();

    private final TestSuiteBuilder<T> b;

    public TestSuiteProcessor(TestSuiteBuilder<T> b) {
        this.b = b;
    }

    public TestSuiteProcessor() {
        this.b = new TestSuiteBuilder<>();
    }

    public TestSuiteBuilder<T> process(Class<T> cls, Supplier<T> supplier) {
        log.debug("start process suite: {}", cls);
        WarmUp warmUp = cls.getAnnotation(WarmUp.class);
        if (warmUp != null)
            b.warmUp(settings(warmUp));

        LoadTest loadTest = cls.getAnnotation(LoadTest.class);
        if (loadTest != null)
            b.settings(settings(loadTest));

        Throttle throttle = cls.getAnnotation(Throttle.class);
        if (throttle != null) {
            Supplier<Limiter> limiter = () -> Limiter.withRate(throttle.threshold(), throttle.time(), throttle.timeUnit());
            if (throttle.shared()) {
                b.limiter(limiter.get());
            } else {
                b.limiter(limiter);
            }
        }

        Delay delay = cls.getAnnotation(Delay.class);
        if (delay != null)
            b.delayer(new RandomDelayer(delay.min(), delay.max(), delay.timeUnit()));

        LoadTestSuite suite = cls.getAnnotation(LoadTestSuite.class);
        if (suite != null) {
           b. name(!suite.name().isEmpty() ? suite.name() : cls.getSimpleName());

            if (suite.shared()) {
                b.sharedInstance(supplier.get());
            } else {
                b.instance(supplier);
            }

        } else {
            // todo may be user should always annotate class with @LoadTestSuite
            b.name(cls.getSimpleName());
        }


        for (Method method : cls.getDeclaredMethods()) {
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers))
                continue;

            log.debug("process suite: {} method: {}", cls, method.getName());
            try {
                BeforeAll beforeAll = method.getAnnotation(BeforeAll.class);
                if (beforeAll != null)
                    buildBeforeAll(method, beforeAll);

                BeforeEach beforeEach = method.getAnnotation(BeforeEach.class);
                if (beforeEach != null)
                    buildBeforeEach(method, beforeEach);

                LoadTestCase testCase = method.getAnnotation(LoadTestCase.class);
                if (testCase != null)
                    buildTestCase(method, testCase);

                AfterEach afterEach = method.getAnnotation(AfterEach.class);
                if (afterEach != null)
                    buildAfterEach(method, afterEach);

                AfterAll afterAll = method.getAnnotation(AfterAll.class);
                if (afterAll != null)
                    buildAfterAll(method, afterAll);

            } catch (Exception e) {
                log.warn("Can't mh method: {}", method.getName(), e);
            }
        }

        return b;
    }

    private Settings settings(LoadTest ann) {
        return new Settings()
                .threadCount(ann.threads())
                .threadIterations(ann.threadIterations())
                .totalIterations(ann.totalIterations())
                .time(ann.time(), ann.timeUnit());
    }

    private Settings settings(WarmUp ann) {
        return new Settings()
                .threadCount(ann.threads())
                .threadIterations(ann.threadIterations())
                .totalIterations(ann.totalIterations())
                .time(ann.time(), ann.timeUnit());
    }


    private void buildBeforeAll(Method method, BeforeAll beforeAll) throws Exception {
        log.trace("Found beforeAll method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        b.beforeAll(beforeAll.onlyOnce(), testCase -> mh.bindTo(testCase).invoke());
    }

    private void buildBeforeEach(Method method, BeforeEach beforeEach) throws Exception {
        log.trace("Found beforeEach method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        b.beforeEach(testCase -> mh.bindTo(testCase).invoke());
    }

    private void buildTestCase(Method method, LoadTestCase loadTest) throws Exception {
        String value = loadTest.value();
        String name = !value.isEmpty() ? value : method.getName();
        log.trace("Found test method: {}, name: {}, disabled: {}", method.getName(), name, loadTest.disabled());

        if (loadTest.disabled())
            return;

        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        AtomicReference<TestCaseMethodWithClick<T>> ref = new AtomicReference<>();
        AtomicBoolean skip = new AtomicBoolean();

        b.testCase(name, loadTest.async(), (suite, clock) -> {
            if (skip.get())
                return;

            TestCaseMethodWithClick<T> proxy = ref.get();
            if (proxy == null) {
                log.debug("init testCase: {} proxy method", name);
                Class<?>[] types = method.getParameterTypes();
                Object[] params = Stream.of(types)
                        .map(param -> {
                            if (param.equals(Clock.class))
                                return clock;

                            skip.set(true);
                            log.warn("Skip test {}: not allowed parameters (only Stopwatch is allowed)", name);
                            throw new RuntimeException("Skip test " + name + ": not allowed parameters (only Stopwatch is allowed)");
                        })
                        .toArray();

                log.trace("bind test method with params: {}", Arrays.toString(params));

                MethodHandle bind = mh.bindTo(suite);

                proxy = types.length == 0 ?
                        (tc, cl) -> bind.invoke() :
                        (tc, cl) -> bind.invokeWithArguments(cl) ;

                ref.set(proxy);
            }

            proxy.run(suite, clock);
        });
    }

    private void buildAfterEach(Method method, AfterEach afterEach) throws Exception {
        log.debug("Found afterEach method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        b.afterEach(suite -> mh.bindTo(suite).invoke());
    }

    private void buildAfterAll(Method method, AfterAll afterAll) throws Exception {
        log.debug("Found afterAll method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        b.afterAll(afterAll.onlyOnce(), suite -> mh.bindTo(suite).invoke());
    }
}
