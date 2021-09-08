package ashes.of.bomber.builder;

import ashes.of.bomber.annotations.AfterEachIteration;
import ashes.of.bomber.annotations.AfterTestCase;
import ashes.of.bomber.annotations.AfterTestSuite;
import ashes.of.bomber.annotations.BeforeEachIteration;
import ashes.of.bomber.annotations.BeforeTestCase;
import ashes.of.bomber.annotations.BeforeTestSuite;
import ashes.of.bomber.annotations.LoadTestCase;
import ashes.of.bomber.annotations.LoadTestSuite;
import ashes.of.bomber.methods.TestCaseMethod;
import ashes.of.bomber.tools.Tools;
import com.google.common.base.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;


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
        log.debug("Start process test suite: {}", cls);
        b.config(config -> config.process(cls));

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

                BeforeEachIteration beforeEach = method.getAnnotation(BeforeEachIteration.class);
                if (beforeEach != null)
                    buildBeforeEach(method, beforeEach);

                LoadTestCase testCase = method.getAnnotation(LoadTestCase.class);
                if (testCase != null)
                    buildTestCase(method, testCase);

                AfterEachIteration afterEach = method.getAnnotation(AfterEachIteration.class);
                if (afterEach != null)
                    buildAfterEach(method, afterEach);

                AfterTestCase afterCase = method.getAnnotation(AfterTestCase.class);
                if (afterCase != null)
                    buildAfterCase(method, afterCase);

                AfterTestSuite afterSuite = method.getAnnotation(AfterTestSuite.class);
                if (afterSuite != null)
                    buildAfterSuite(method, afterSuite);

            } catch (Exception e) {
                log.warn("Can't process test suite class: {} method: {}", cls.getName(), method.getName(), e);
//                throw new RuntimeException(e);
            }
        }

        return b;
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
    
    private void buildBeforeEach(Method method, BeforeEachIteration beforeEach) throws Exception {
        log.debug("Found @BeforeEachIteration method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        b.beforeEach(instance -> mh.bindTo(instance).invoke());
    }

    private void buildTestCase(Method method, LoadTestCase loadTest) {
        String value = loadTest.value();
        String name = !value.isEmpty() ? value : method.getName();
        log.debug("Found @LoadTestCase method: {}, name: {}, disabled: {}", method.getName(), name, loadTest.disabled());

        if (loadTest.disabled())
            return;

        Class<?>[] types = method.getParameterTypes();
        if (types.length > 1) {
            throw new RuntimeException("Build test case: " + name + " failed. Only one parameter with type: " + Tools.class.getName() + " is allowed)");
        }

        for (Class<?> type : types) {
            if (!type.equals(Tools.class))
                throw new RuntimeException("Build test case: " + name + " failed. Not allowed parameter: " + type.getName()  + ", only " + Tools.class.getName() + " is allowed");
        }

        b.testCase(builder -> builder
                .name(name)
                .async(loadTest.async())
                .config(config -> config.process(method))
                .test(buildTestCaseMethod(method))
        );
    }

    private TestCaseMethod<T> buildTestCaseMethod(Method method) {
        try {
            MethodHandle mh = MethodHandles.lookup().unreflect(method);
            AtomicReference<TestCaseMethod<T>> ref = new AtomicReference<>();
            if (method.getParameterTypes().length == 0) {
                return (suite, tools) -> {
                    TestCaseMethod<T> proxy = ref.get();
                    if (proxy == null) {
                        log.debug("Init test case method: {} proxy", method.getName());
                        MethodHandle bind = mh.bindTo(suite);

                        proxy = (o, t) -> bind.invoke();
                        ref.set(proxy);
                    }

                    proxy.run(suite, tools);
                };
            } else {
                return (suite, tools) -> {
                    TestCaseMethod<T> proxy = ref.get();
                    if (proxy == null) {
                        log.debug("Init test case method: {} proxy", method.getName());
                        MethodHandle bind = mh.bindTo(suite);

                        proxy = (o, t) -> bind.invokeWithArguments(t);
                        ref.set(proxy);
                    }

                    proxy.run(suite, tools);
                };
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void buildAfterEach(Method method, AfterEachIteration afterEach) throws Exception {
        log.debug("Found @AfterEachIteration method: {}", method.getName());
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
