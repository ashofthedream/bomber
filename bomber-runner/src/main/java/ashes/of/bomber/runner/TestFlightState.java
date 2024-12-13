package ashes.of.bomber.runner;

import ashes.of.bomber.flight.plan.TestFlightPlan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.function.BooleanSupplier;


public class TestFlightState {
    private static final Logger log = LogManager.getLogger();

    private final TestFlightPlan plan;
    private final BooleanSupplier condition;
    private final Instant startTime = Instant.now();

    @Nullable
    private volatile Instant finishTime;

    @Nullable
    private volatile TestAppState current;

    public TestFlightState(TestFlightPlan plan, BooleanSupplier condition) {
        this.plan = plan;
        this.condition = condition;
    }

    public TestFlightPlan getPlan() {
        return plan;
    }

    public BooleanSupplier getCondition() {
        return condition;
    }

    public Instant getStartTime() {
        return startTime;
    }

    @Nullable
    public Instant getFinishTime() {
        return finishTime;
    }

    @Nullable
    public TestAppState getCurrentApp() {
        return current;
    }

    public TestFlightState setFinishTime(Instant finishTime) {
        this.finishTime = finishTime;
        return this;
    }

    public void attach(TestAppState state) {
        current = state;
    }
}
