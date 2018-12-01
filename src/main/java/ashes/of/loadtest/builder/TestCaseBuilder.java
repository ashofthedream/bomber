package ashes.of.loadtest.builder;

import ashes.of.loadtest.*;
import ashes.of.loadtest.runner.TestCaseRunner;
import ashes.of.loadtest.sink.Sink;
import ashes.of.loadtest.throttler.Limiter;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;



public class TestCaseBuilder<T extends TestCase> {
    private static final Logger log = LogManager.getLogger(TestCaseBuilder.class);

    private final List<LifeCycle<T>> beforeAll = new ArrayList<>();
    private final List<LifeCycle<T>> beforeEach = new ArrayList<>();
    private final List<LifeCycle<T>> afterEach = new ArrayList<>();
    private final List<LifeCycle<T>> afterAll = new ArrayList<>();

    private final Map<String, TestWithStopwatch<T>> tests = new LinkedHashMap<>();
    private final Map<String, TestWithStopwatch<T>> noop = ImmutableMap.of("noop", (test, stopwatch) -> {});

    private final List<Sink> sinks = new ArrayList<>();
    private final SettingsBuilder settings = new SettingsBuilder();


    private String name;
    private Supplier<T> testCase;
    private Supplier<Limiter> limiter = Limiter::alwaysPermit;



    public SettingsBuilder settings() {
        return settings;
    }


    public TestCaseBuilder<T> name(String name) {
        this.name = name;
        return this;
    }


    public TestCaseBuilder<T> settings(Consumer<SettingsBuilder> builder) {
        builder.accept(settings);
        return this;
    }

    public TestCaseBuilder<T> settings(SettingsBuilder builder) {
        settings.baseline(builder.getBaseline())
                .warmUp(builder.getWarmUp())
                .test(builder.getTest());


        return this;
    }

    /**
     * @param limiter shared request limiter
     * @return builder
     */
    public TestCaseBuilder<T> limiter(Limiter limiter) {
        return limiter(() -> limiter);
    }

    public TestCaseBuilder<T> limiter(Supplier<Limiter> limiter) {
        this.limiter = limiter;
        return this;
    }


    public TestCaseBuilder<T> sink(Sink sink) {
        this.sinks.add(sink);
        return this;
    }

    public TestCaseBuilder<T> sinks(List<Sink> sinks) {
        this.sinks.addAll(sinks);
        return this;
    }


    public TestCaseBuilder<T> testCase(Supplier<T> testCase) {
        Preconditions.checkNotNull(testCase, "TestCase is null");
        this.testCase = testCase;
        return this;
    }

    public TestCaseBuilder<T> testCase(T testCase) {
        Preconditions.checkNotNull(testCase, "TestCase is null");
        return testCase(() -> testCase);
    }


    public TestCaseBuilder<T> beforeAll(LifeCycle<T> before) {
        beforeAll.add(before);
        return this;
    }

    public TestCaseBuilder<T> beforeEach(LifeCycle<T> before) {
        beforeEach.add(before);
        return this;
    }

    public TestCaseBuilder<T> test(String name, TestNoArgs<T> test) {
        return test(name, (tc, stopwatch) -> test.run(tc));
    }

    public TestCaseBuilder<T> test(String name, TestWithStopwatch<T> test) {
        this.tests.put(name, test);
        return this;
    }

    public TestCaseBuilder<T> afterEach(LifeCycle<T> after) {
        afterEach.add(after);
        return this;
    }

    public TestCaseBuilder<T> afterAll(LifeCycle<T> after) {
        afterAll.add(after);
        return this;
    }


    public Runnable build() {
        Preconditions.checkNotNull(name,     "Name is null");
        Preconditions.checkNotNull(testCase, "TestCase is null");

        return this::run;

    }

    private void run() {
        try {
            log.info("Start testCase: {}", name);

            new TestCaseRunner<>(name, Stage.Baseline, settings.getBaseline(), sinks, beforeAll, beforeEach,  noop, testCase, afterEach, afterAll, limiter).run();
            new TestCaseRunner<>(name, Stage.WarmUp,   settings.getWarmUp(),   sinks, beforeAll, beforeEach, tests, testCase, afterEach, afterAll, limiter).run();
            new TestCaseRunner<>(name, Stage.Test,     settings.getTest(),     sinks, beforeAll, beforeEach, tests, testCase, afterEach, afterAll, limiter).run();

            log.info("End testCase: {}", name);
        } catch (Exception e) {
            log.warn("Some shit happened", e);
        }
    }
}

