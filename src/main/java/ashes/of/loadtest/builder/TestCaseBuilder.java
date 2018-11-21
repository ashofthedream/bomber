package ashes.of.loadtest.builder;

import ashes.of.loadtest.*;
import ashes.of.loadtest.runner.TestCaseRunner;
import ashes.of.loadtest.sink.Sink;
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


    private String name;
    private Supplier<T> testCase;

    private final Map<String, Test<T>> tests = new LinkedHashMap<>();
    private final Map<String, Test<T>> noop = ImmutableMap.of("noop", (test, stopwatch) -> {});

    private final List<Sink> sinks = new ArrayList<>();
    private final SettingsBuilder settings = new SettingsBuilder();


    public TestCaseBuilder<T> name(String name) {
        this.name = name;
        return this;
    }


    public TestCaseBuilder<T> settings(Consumer<SettingsBuilder> consumer) {
        consumer.accept(settings);
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
            new TestCaseRunner<>(name, Stage.Baseline, settings.getBaseline(), sinks, noop,  testCase).run();
            new TestCaseRunner<>(name, Stage.WarmUp,   settings.getWarmUp(),   sinks, tests, testCase).run();
            new TestCaseRunner<>(name, Stage.Test,     settings.getTest(),     sinks, tests, testCase).run();

            log.info("End testCase: {}", name);
        } catch (Exception e) {
            log.warn("Some shit happened", e);
        }
    }


}
