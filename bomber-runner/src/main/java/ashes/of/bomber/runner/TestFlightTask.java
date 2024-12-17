package ashes.of.bomber.runner;

import ashes.of.bomber.core.TestSuiteGroup;
import ashes.of.bomber.core.TestSuite;
import ashes.of.bomber.events.EventMachine;
import ashes.of.bomber.events.FlightFinishedEvent;
import ashes.of.bomber.events.FlightStartedEvent;
import ashes.of.bomber.flight.plan.TestFlightPlan;
import ashes.of.bomber.snapshots.TestCaseSnapshot;
import ashes.of.bomber.snapshots.TestFlightSnapshot;
import ashes.of.bomber.snapshots.TestSuiteSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;


public class TestFlightTask implements Runnable {
    private static final Logger log = LogManager.getLogger();

    private final TestFlightPlan flightPlan;
    private final EventMachine em;
    private final List<TestSuiteGroup> apps;

    private final CountDownLatch endLatch = new CountDownLatch(1);
    private final BooleanSupplier condition = () -> endLatch.getCount() > 0;

    private final List<TestSuiteTask> children = new CopyOnWriteArrayList<>();

    private volatile Instant startTime;
    private volatile Instant finishTime;

    public TestFlightTask(TestFlightPlan flightPlan, EventMachine em, List<TestSuiteGroup> apps) {
        this.flightPlan = flightPlan;
        this.em = em;
        this.apps = apps;
    }

    public TestFlightPlan getPlan() {
        return flightPlan;
    }

    public BooleanSupplier getCondition() {
        return condition;
    }

    @Nullable
    public Instant getStartTime() {
        return startTime;
    }

    @Nullable
    public Instant getFinishTime() {
        return finishTime;
    }

    @Nullable
    public TestSuiteTask getCurrentChild() {
        if (children.isEmpty()) {
            return null;
        }

        return children.get(children.size() - 1);
    }

    /**
     * Runs the test flight
     */
    @Override
    public void run() {
        log.info("Start TestFlightTask");
        ThreadContext.put("flightId", String.valueOf(flightPlan.flightId()));
        startTime = Instant.now();
        em.dispatch(new FlightStartedEvent(startTime, flightPlan.flightId()));

        try {
            var testSuitesByName = apps.stream()
                    .map(TestSuiteGroup::testSuites)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toMap(TestSuite::name, app -> (TestSuite<Object>) app));

            flightPlan.testSuites()
                    .forEach(plan -> {
                        var testSuite = testSuitesByName.get(plan.name());
                        if (testSuite == null) {
                            log.warn("Bomber has no test suite with name: {}", plan.name());
                            return;
                        }

                        // todo return report with error? but now fail fast approach used
                        var child = new TestSuiteTask( this, em, plan, testSuite);
                        children.add(child);

                        child.run();
                    });


            finishTime = Instant.now();
            em.dispatch(new FlightFinishedEvent(finishTime, flightPlan.flightId()));

            ThreadContext.clearAll();
            log.info("Flight finished: {}", flightPlan.flightId());
        } finally {
            ThreadContext.clearAll();
        }
    }



    @Deprecated
    @Nullable
    public TestFlightSnapshot getSnapshot() {
        var current = getCurrentChild();

        var workers = Optional.ofNullable(current)
//                .map(TestAppTask::getCurrentChild)
                .map(TestSuiteTask::getExecutor)
                .map(WorkerPool::getWorkers)
                .map(list -> list.stream().map(Worker::getSnapshot).toList())
                .orElseGet(List::of);

        return new TestFlightSnapshot(
                flightPlan,
                toSnapshot(current),
                workers
        );
    }

    private TestSuiteSnapshot toSnapshot(TestSuiteTask task) {
        if (task == null) {
            return null;
        }

        return new TestSuiteSnapshot(
                task.getTestSuite().name(),
                toSnapshot(task.getCurrentChild())
        );
    }

    private TestCaseSnapshot toSnapshot(TestCaseTask task) {
        if (task == null) {
            return null;
        }

        return new TestCaseSnapshot(
                task.getTestCase().name(),
                task.getConfiguration().settings(),
                task.getStartTime(),
                task.getFinishTime(),
                task.getIterationsCount(),
                task.getErrorCount()
        );
    }

    public void stop() {
        endLatch.countDown();
    }

    public void await() throws InterruptedException {
        endLatch.await();
    }
}
