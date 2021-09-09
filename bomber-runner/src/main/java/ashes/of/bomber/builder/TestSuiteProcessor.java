package ashes.of.bomber.builder;

import ashes.of.bomber.annotations.AfterEachIteration;
import ashes.of.bomber.annotations.AfterTestCase;
import ashes.of.bomber.annotations.AfterTestSuite;
import ashes.of.bomber.annotations.BeforeEachIteration;
import ashes.of.bomber.annotations.BeforeTestCase;
import ashes.of.bomber.annotations.BeforeTestSuite;
import ashes.of.bomber.annotations.LoadTestCase;
import ashes.of.bomber.annotations.LoadTestSuite;
import com.google.common.base.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;


public class TestSuiteProcessor<T> {
    private static final Logger log = LogManager.getLogger();

    private final TestSuiteBuilder<T> builder;

    public TestSuiteProcessor(TestSuiteBuilder<T> builder) {
        this.builder = builder;
    }

    public TestSuiteBuilder<T> process(Class<T> cls, Supplier<T> supplier) {
        log.debug("Start process test suite: {}", cls);
        builder.config(config -> config.process(cls));

        LoadTestSuite suite = cls.getAnnotation(LoadTestSuite.class);
        if (suite != null) {
            builder. name(!Strings.isNullOrEmpty(suite.name()) ? suite.name() : cls.getSimpleName());

            if (suite.shared()) {
                builder.sharedInstance(supplier.get());
            } else {
                builder.instance(supplier);
            }

        } else {
            // todo may be user should always annotate class with @LoadTestSuite
            builder.name(cls.getSimpleName());
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

        return builder;
    }

    private void buildTestCase(Method method, LoadTestCase testCase) {
        if (testCase.disabled()) {
            log.debug("Test case: {} is disabled", method.getName());
            return;
        }

        builder.testCase(builder -> {
            new TestCaseProcessor<T>(builder)
                    .process(method, testCase);
        });
    }

    private void buildBeforeSuite(Method method, BeforeTestSuite beforeAll) throws Exception {
        log.debug("Found @BeforeTestSuite method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        builder.beforeSuite(beforeAll.onlyOnce(), instance -> mh.bindTo(instance).invoke());
    }

    private void buildBeforeCase(Method method, BeforeTestCase beforeAll) throws Exception {
        log.debug("Found @BeforeTestCase method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        builder.beforeCase(beforeAll.onlyOnce(), instance -> mh.bindTo(instance).invoke());
    }
    
    private void buildBeforeEach(Method method, BeforeEachIteration beforeEach) throws Exception {
        log.debug("Found @BeforeEachIteration method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        builder.beforeEach(instance -> mh.bindTo(instance).invoke());
    }

    private void buildAfterEach(Method method, AfterEachIteration afterEach) throws Exception {
        log.debug("Found @AfterEachIteration method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        builder.afterEach(instance -> mh.bindTo(instance).invoke());
    }

    private void buildAfterCase(Method method, AfterTestCase afterAll) throws Exception {
        log.debug("Found @AfterTestCase method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        builder.afterCase(afterAll.onlyOnce(), instance -> mh.bindTo(instance).invoke());
    }

    private void buildAfterSuite(Method method, AfterTestSuite afterAll) throws Exception {
        log.debug("Found @AfterTestSuite method: {}", method.getName());
        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        builder.afterSuite(afterAll.onlyOnce(), instance -> mh.bindTo(instance).invoke());
    }
}
