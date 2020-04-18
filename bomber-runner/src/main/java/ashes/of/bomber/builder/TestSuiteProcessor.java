package ashes.of.bomber.builder;

import ashes.of.bomber.annotations.*;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.delayer.RandomDelayer;
import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.methods.TestCaseMethodWithTools;
import ashes.of.bomber.tools.Tools;
import com.google.common.base.Strings;
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
            b. name(!Strings.isNullOrEmpty(suite.name()) ? suite.name() : cls.getSimpleName());

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

            log.trace("process suite: {} method: {}", cls, method.getName());
            try {
                BeforeTestSuite beforeSuite = method.getAnnotation(BeforeTestSuite.class);
                if (beforeSuite != null)
                    buildBeforeSuite(method, beforeSuite);
                
                BeforeTestCase beforeCase = method.getAnnotation(BeforeTestCase.class);
                if (beforeCase != null)
                    buildBeforeCase(method, beforeCase);
                
                BeforeEach beforeEach = method.getAnnotation(BeforeEach.class);
                if (beforeEach != null)
                    buildBeforeEach(method, beforeEach);

                LoadTestCase testCase = method.getAnnotation(LoadTestCase.class);
                if (testCase != null)
                    buildTestCase(method, testCase);

                AfterEach afterEach = method.getAnnotation(AfterEach.class);
                if (afterEach != null)
                    buildAfterEach(method, afterEach);

                AfterTestCase afterCase = method.getAnnotation(AfterTestCase.class);
                if (afterCase != null)
                    buildAfterCase(method, afterCase);
                
                AfterTestSuite afterSuite = method.getAnnotation(AfterTestSuite.class);
                if (afterSuite != null)
                    buildAfterSuite(method, afterSuite);

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


    private void buildBeforeSuite(Method method, BeforeTestSuite beforeAll) throws Exception {
        log.debug("Found @BeforeTestSuite method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        b.beforeSuite(beforeAll.onlyOnce(), instance -> mh.bindTo(instance).invoke());
    }

    private void buildBeforeCase(Method method, BeforeTestCase beforeAll) throws Exception {
        log.debug("Found @BeforeTestCase method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        b.beforeCase(beforeAll.onlyOnce(), instance -> mh.bindTo(instance).invoke());
    }
    
    private void buildBeforeEach(Method method, BeforeEach beforeEach) throws Exception {
        log.debug("Found #BeforeEach method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        b.beforeEach(instance -> mh.bindTo(instance).invoke());
    }

    private void buildTestCase(Method method, LoadTestCase loadTest) throws Exception {
        String value = loadTest.value();
        String name = !value.isEmpty() ? value : method.getName();
        log.debug("Found @LoadTestCase method: {}, name: {}, disabled: {}", method.getName(), name, loadTest.disabled());

        if (loadTest.disabled())
            return;

        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        AtomicReference<TestCaseMethodWithTools<T>> ref = new AtomicReference<>();
        AtomicBoolean skip = new AtomicBoolean();

        b.testCase(name, loadTest.async(), (suite, tools) -> {
            if (skip.get())
                return;

            TestCaseMethodWithTools<T> proxy = ref.get();
            if (proxy == null) {
                log.debug("init testCase: {} proxy method", name);
                Class<?>[] types = method.getParameterTypes();
                Object[] params = Stream.of(types)
                        .map(param -> {
                            if (param.equals(Tools.class))
                                return tools;

                            skip.set(true);
                            log.warn("Skip test {}: not allowed parameters (only {} is allowed)", name, Tools.class.getName());
                            throw new RuntimeException("Skip test " + name + ": not allowed parameters (only " + Tools.class.getName() + " is allowed)");
                        })
                        .toArray();

                log.trace("bind test method with params: {}", Arrays.toString(params));

                MethodHandle bind = mh.bindTo(suite);

                proxy = types.length == 0 ?
                        (tc, t) -> bind.invoke() :
                        (tc, t) -> bind.invokeWithArguments(t) ;

                ref.set(proxy);
            }

            proxy.run(suite, tools);
        });
    }

    private void buildAfterEach(Method method, AfterEach afterEach) throws Exception {
        log.debug("Found @AfterEach method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        b.afterEach(instance -> mh.bindTo(instance).invoke());
    }

    private void buildAfterCase(Method method, AfterTestCase afterAll) throws Exception {
        log.debug("Found @AfterTestCase method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        b.afterCase(afterAll.onlyOnce(), instance -> mh.bindTo(instance).invoke());
    }

    private void buildAfterSuite(Method method, AfterTestSuite afterAll) throws Exception {
        log.debug("Found @AfterTestSuite method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        b.afterSuite(afterAll.onlyOnce(), instance -> mh.bindTo(instance).invoke());
    }
}
