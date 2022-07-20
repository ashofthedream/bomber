package ashes.of.bomber.processors;

import ashes.of.bomber.annotations.LoadTestCase;
import ashes.of.bomber.builder.TestCaseBuilder;
import ashes.of.bomber.methods.TestCaseMethod;
import ashes.of.bomber.tools.Tools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

public class TestCaseProcessor<T> {
    private static final Logger log = LogManager.getLogger();

    private final TestCaseBuilder<T> builder;

    public TestCaseProcessor(TestCaseBuilder<T> builder) {
        this.builder = builder;
    }

    public void process(Method method, LoadTestCase loadTest) {
        String value = loadTest.value();
        String name = !value.isEmpty() ? value : method.getName();
        log.debug("Found @LoadTestCase method: {}, name: {}, disabled: {}", method.getName(), name, loadTest.disabled());


        Class<?>[] types = method.getParameterTypes();
        if (types.length > 1) {
            throw new RuntimeException("Build test case: " + name + " failed. Only one parameter with type: " + Tools.class.getName() + " is allowed)");
        }

        for (Class<?> type : types) {
            if (!type.equals(Tools.class))
                throw new RuntimeException("Build test case: " + name + " failed. Not allowed parameter: " + type.getName()  + ", only " + Tools.class.getName() + " is allowed");
        }

        builder .name(name)
                .async(loadTest.async())
                .config(config -> config.process(method))
                .test(buildTestCaseMethod(method));
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
}
