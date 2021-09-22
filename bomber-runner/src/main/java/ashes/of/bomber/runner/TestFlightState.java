package ashes.of.bomber.runner;

import ashes.of.bomber.flight.plan.TestFlightPlan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.function.BooleanSupplier;


public class TestFlightState {
    private static final Logger log = LogManager.getLogger();

    private final BooleanSupplier condition;
    private final TestFlightPlan plan;
    private final Instant startTime = Instant.now();
    private volatile Instant finishTime;

//    private final List<TestAppState> testApps = new CopyOnWriteArrayList<>();

    @Nullable
    private volatile TestAppState current;

    public TestFlightState(TestFlightPlan plan, BooleanSupplier condition) {
        this.plan = plan;
        this.condition = condition;
    }

    public TestFlightPlan getPlan() {
        return plan;
    }

    public TestAppState getCurrent() {
        return current;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getFinishTime() {
        return finishTime;
    }

    public TestFlightState setFinishTime(Instant finishTime) {
        this.finishTime = finishTime;
        return this;
    }


    public void attach(TestAppState state) {
//        testApps.add(state);
        current = state;
    }

    public BooleanSupplier getCondition() {
        return condition;
    }
}
