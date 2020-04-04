package ashes.of.bomber.core;

import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.watcher.Watcher;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public interface BomberApp {

    Plan getPlan();

    void add(Sink sink);

    void add(long ms, Watcher watcher);

    default void add(Duration duration, Watcher watcher) {
        add(duration.toMillis(), watcher);
    }

    StateModel getState();

    String getName();

    List<TestSuiteModel> getTestSuites();

    @Deprecated
    default Plan createDefaultPlan(long id) {
        List<SuitePlan> suites = getTestSuites().stream()
                .map(testSuite -> {
                    List<String> cases = testSuite.getTestCases().stream()
                            .map(TestCaseModel::getName)
                            .collect(Collectors.toList());

                    return new SuitePlan(testSuite.getName(), cases);
                })
                .collect(Collectors.toList());


        return new Plan(id, suites);
    }

    default Report start() {
        return start(createDefaultPlan(System.currentTimeMillis()));
    }

    default CompletableFuture<Report> startAsync() {
        return CompletableFuture.supplyAsync(this::start);
    }

    Report start(Plan plan);

    default CompletableFuture<Report> startAsync(Plan plan) {
        return CompletableFuture.supplyAsync(() -> start(plan));
    }

    void await() throws InterruptedException;

    void stop();


}
