package ashes.of.bomber.core;

import ashes.of.bomber.flight.FlightReport;
import ashes.of.bomber.flight.FlightPlan;
import ashes.of.bomber.flight.TestCasePlan;
import ashes.of.bomber.flight.TestSuitePlan;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.watcher.Watcher;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public interface BomberApp {

    FlightPlan getFlightPlan();

    void add(Sink sink);

    void add(long ms, Watcher watcher);

    default void add(Duration duration, Watcher watcher) {
        add(duration.toMillis(), watcher);
    }

    StateModel getState();

    String getName();

    List<TestSuiteModel> getTestSuites();

    @Deprecated
    default FlightPlan createDefaultPlan(long id) {
        List<TestSuitePlan> suites = getTestSuites().stream()
                .map(testSuite -> {
                    List<TestCasePlan> cases = testSuite.getTestCases().stream()
                            .map(testCase -> new TestCasePlan(testCase.getName()))
                            .collect(Collectors.toList());

                    return new TestSuitePlan(testSuite.getName(), cases);
                })
                .collect(Collectors.toList());


        return new FlightPlan(id, suites);
    }

    default FlightReport start() {
        return start(createDefaultPlan(System.currentTimeMillis()));
    }

    default CompletableFuture<FlightReport> startAsync() {
        return CompletableFuture.supplyAsync(this::start);
    }

    FlightReport start(FlightPlan plan);

    default CompletableFuture<FlightReport> startAsync(FlightPlan plan) {
        return CompletableFuture.supplyAsync(() -> start(plan));
    }

    void await() throws InterruptedException;

    void stop();
}
