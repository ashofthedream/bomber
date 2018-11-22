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

    private final Map<String, Test<T>> tests = new LinkedHashMap<>();
    private final Map<String, Test<T>> noop = ImmutableMap.of("noop", (test, stopwatch) -> {});

    private final List<Sink> sinks = new ArrayList<>();
    private final SettingsBuilder settings = new SettingsBuilder();


    private String name;
    private Supplier<T> testCase;
    private Supplier<Limiter> limiter = Limiter::alwaysPass;



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

    public TestCaseBuilder<T> test(String name, Test<T> test) {
        this.tests.put(name, test);
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
            new TestCaseRunner<>(name, Stage.Baseline, settings.getBaseline(), sinks, noop,  testCase, limiter).run();
            new TestCaseRunner<>(name, Stage.WarmUp,   settings.getWarmUp(),   sinks, tests, testCase, limiter).run();
            new TestCaseRunner<>(name, Stage.Test,     settings.getTest(),     sinks, tests, testCase, limiter).run();

            log.info("End testCase: {}", name);
        } catch (Exception e) {
            log.warn("Some shit happened", e);
        }
    }


}
