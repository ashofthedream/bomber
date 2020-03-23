package ashes.of.bomber.builder;

import ashes.of.bomber.annotations.*;
import ashes.of.bomber.core.Settings;
import ashes.of.bomber.limiter.Limiter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class TestAppProcessor {
    private static final Logger log = LogManager.getLogger();

    private final TestAppBuilder b = new TestAppBuilder();
    private final AtomicReference<Object> app = new AtomicReference<>();

    public TestAppBuilder process(Class<?> cls) {
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

        for (Method method : cls.getDeclaredMethods()) {
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers))
                continue;

            log.debug("check cls: {} method: {}", cls.getClass(), method.getName());
            try {
                Provide provide = method.getAnnotation(Provide.class);
                if (provide != null)
                    provide(cls, method, provide);

            } catch (Exception e) {
                log.warn("Can't mh method: {}", method.getName(), e);
            }
        }

        LoadTestApp app = cls.getAnnotation(LoadTestApp.class);
        if (app == null)
            throw new RuntimeException("Test application class should be market with @LoadTestApp");

        if (app.testSuites().length < 1)
            throw new RuntimeException("Test application class should contains at least one test suite");

        for (Class<?> testSuiteClass : app.testSuites()) {
            log.debug("add testSuiteClass: {}", testSuiteClass);
            b.testSuiteClass(testSuiteClass);
        }

        return b;
    }

    private void provide(Class<?> cls, Method method, Provide provide) throws IllegalAccessException {
        MethodHandle mh = MethodHandles.lookup().unreflect(method);

        Class<?> ret = method.getReturnType();
        b.provide(ret, () -> {
            try {
                Object app = cls.getConstructor().newInstance();
                return ret.cast(mh.bindTo(app).invoke());
            } catch (Throwable e) {
                log.warn("Can't provide: {}", ret);
                throw new RuntimeException(e);
            }
        });
    }

    private Settings settings(LoadTest ann) {
        return new Settings()
                .threadCount(ann.threads())
                .threadInvocationCount(ann.threadInvocations())
                .totalInvocationCount(ann.totalInvocations())
                .time(ann.time(), ann.timeUnit());
    }

    private Settings settings(WarmUp ann) {
        return new Settings()
                .threadCount(ann.threads())
                .threadInvocationCount(ann.threadInvocations())
                .totalInvocationCount(ann.totalInvocations())
                .time(ann.time(), ann.timeUnit());
    }
}
