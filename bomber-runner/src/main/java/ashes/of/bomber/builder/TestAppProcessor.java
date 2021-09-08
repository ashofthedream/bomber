package ashes.of.bomber.builder;

import ashes.of.bomber.annotations.LoadTestApp;
import ashes.of.bomber.annotations.LoadTestSuite;
import ashes.of.bomber.annotations.Provide;
import com.google.common.base.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicReference;


public class TestAppProcessor {
    private static final Logger log = LogManager.getLogger();

    private final TestAppBuilder b = new TestAppBuilder();
    private final AtomicReference<Object> app = new AtomicReference<>();

    public TestAppBuilder process(Class<?> cls) {
        log.debug("Start process app: {} ", cls);
        b.config(env -> env.process(cls));

        for (Method method : cls.getDeclaredMethods()) {
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers))
                continue;

            log.debug("Process app: {}, method: {}", cls, method.getName());
            try {
                Provide provide = method.getAnnotation(Provide.class);
                if (provide != null)
                    provide(cls, method, provide);

            } catch (Exception e) {
                log.warn("Can't process method: {}", method.getName(), e);
            }
        }

        LoadTestApp app = cls.getAnnotation(LoadTestApp.class);
        if (app == null)
            throw new RuntimeException("Test application class should be market with @LoadTestApp");

        LoadTestSuite suite = cls.getAnnotation(LoadTestSuite.class);
        if (app.testSuites().length < 1 && suite == null)
            throw new RuntimeException("Test application class should contains at least one test suite @LoadTestApp(testSuites = {ExampleTestSuite.class}) or marked by @LoadTestSuite");

        b.name(!Strings.isNullOrEmpty(app.name()) ? app.name() : cls.getSimpleName());
        b.testSuiteClass(cls);

        for (Class<?> testSuiteClass : app.testSuites()) {
            log.debug("Add testSuiteClass: {}", testSuiteClass);
            b.testSuiteClass(testSuiteClass);
        }

        return b;
    }

    private void provide(Class<?> cls, Method method, Provide provide) throws IllegalAccessException {
        Class<?> ret = method.getReturnType();

        MethodHandle mh = MethodHandles.lookup().unreflect(method);
        AtomicReference<Object> object = new AtomicReference<>();

        b.provide(ret, () -> {

            Object res = object.get();
            if (res == null) {
                log.debug("init provider type: {}", ret);
                try {
                    Object a = getOrCreateApp(cls);

                    Object result = mh.bindTo(a).invoke();
                    object.set(result);

                    return result;
                } catch (Throwable e) {
                    log.warn("Can't create binding: {}", ret);
                    throw new RuntimeException(e);
                }
            }

            return res;
        });
    }

    private Object getOrCreateApp(Class<?> cls) throws Throwable {
        Object a = app.get();
        if (a == null) {
            log.debug("init provider app: {}", cls);

            Object created = cls.getConstructor().newInstance();
            app.set(created);
            return created;
        }

        return a;
    }
}
