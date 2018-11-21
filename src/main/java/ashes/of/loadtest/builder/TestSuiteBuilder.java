package ashes.of.loadtest.builder;

import ashes.of.loadtest.TestCase;
import ashes.of.loadtest.annotations.*;
import ashes.of.loadtest.settings.Settings;
import ashes.of.loadtest.sink.Sink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class TestSuiteBuilder<TC extends TestCase> {
    private static final Logger log = LogManager.getLogger(TestSuiteBuilder.class);

    /**
     * Test cases for run
     */
    private final List<TestCaseBuilder<TC>> testCases = new ArrayList<>();
    private final List<Sink> sinks = new ArrayList<>();
    private final SettingsBuilder settings = new SettingsBuilder();


    public TestSuiteBuilder<TC> settings(Consumer<SettingsBuilder> consumer) {
        consumer.accept(settings);
        return this;
    }


    public TestSuiteBuilder<TC> sink(Sink sink) {
        this.sinks.add(sink);
        return this;
    }

    public TestSuiteBuilder<TC> sinks(List<Sink> sinks) {
        this.sinks.addAll(sinks);
        return this;
    }

    
    private <F extends TestCase> TestCaseBuilder<TC> newTestCase() {
        return new TestCaseBuilder<TC>()
                .settings(b -> b.copyOf(settings))
                .sinks(sinks);
    }

    public TestSuiteBuilder<TC> testCase(Consumer<TestCaseBuilder<TC>> consumer) {
        TestCaseBuilder<TC> b = newTestCase();
        consumer.accept(b);

        testCases.add(b);
        return this;
    }

    public TestSuiteBuilder<TC> testCase(Class<TC> cls) {
        TestCaseBuilder<TC> b = newTestCase();

        Baseline baseline = cls.getAnnotation(Baseline.class);
        if (baseline != null)
            b.settings(s -> s.baseline(makeSettings(baseline)));

        WarmUp warmUp = cls.getAnnotation(WarmUp.class);
        if (warmUp != null)
            b.settings(s -> s.warmUp(makeSettings(warmUp)));

        Supplier<TC> supplier = () -> {
            try {
                return cls.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create new instance of test case", e);
            }
        };

        LoadTestCase test = cls.getAnnotation(LoadTestCase.class);
        if (test != null) {
            b       .name(!test.name().isEmpty() ? test.name() : cls.getSimpleName())
                    .settings(s -> s.test(makeSettings(test)));


            if (test.concurrent()) {
                b.testCase(supplier.get());
            } else {
                b.testCase(supplier);
            }

        } else {
            b.name(cls.getSimpleName());
        }

        for (Method method : cls.getDeclaredMethods()) {
            BeforeAll beforeAll = method.getAnnotation(BeforeAll.class);
            if (beforeAll != null) {
                log.debug("Found beforeAll method: {}", method.getName());
            }


            BeforeLoadTest beforeTest = method.getAnnotation(BeforeLoadTest.class);
            if (beforeAll != null) {
                log.debug("Found beforeTest method: {}", method.getName());
            }


            LoadTest loadTest = method.getAnnotation(LoadTest.class);
            if (loadTest != null) {
                log.debug("Found test method: {}", method.getName());

                try {
                    String value = loadTest.value();

                    MethodHandle mh = MethodHandles.lookup().unreflect(method);
                    String name = !value.isEmpty() ? value : method.getName();
                    b.test(name, (testCase, stopwatch) -> mh.bindTo(testCase).invoke(stopwatch));
                } catch (Exception e) {
                    log.warn("Can't mh method: {}", method.getName(), e);
                }
            }


            AfterLoadTest afterTest = method.getAnnotation(AfterLoadTest.class);
            if (afterTest != null) {
                log.debug("Found afterTest method: {}", method.getName());
            }


            AfterAll afterAll = method.getAnnotation(AfterAll.class);
            if (afterAll != null) {
                log.debug("Found afterAll method: {}", method.getName());
            }
        }

        testCases.add(b);
        return this;
    }


    private Settings makeSettings(LoadTestCase ann) {
        return new Settings()
                .threads(ann.threads())
                .threadIterationCount(ann.threadInvocations())
                .totalIterationCount(ann.totalInvocations())
                .time(ann.time(), ann.timeUnit());
    }

    private Settings makeSettings(WarmUp ann) {
        return new Settings()
                .threads(ann.threads())
                .threadIterationCount(ann.threadInvocations())
                .totalIterationCount(ann.totalInvocations())
                .time(ann.time(), ann.timeUnit())
                .setDisabled(ann.disabled());
    }

    private Settings makeSettings(Baseline ann) {
        return new Settings()
                .time(ann.time(), ann.timeUnit())
                .setDisabled(ann.disabled());
    }


    /**
     * Run all test cases
     */
    public void run() {
        testCases.stream()
                .map(TestCaseBuilder::build)
                .forEach(Runnable::run);
    }
}
