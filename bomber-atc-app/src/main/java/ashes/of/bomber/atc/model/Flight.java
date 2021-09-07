package ashes.of.bomber.atc.model;

import ashes.of.bomber.flight.FlightPlan;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Flight {
    private final FlightPlan plan;
    private final Map<String, FlightProgress> progress = new ConcurrentHashMap<>();
    private volatile long startedAt;
    private volatile long finishedAt;

    public Flight(FlightPlan plan) {
        this.plan = plan;
    }

    public FlightPlan getPlan() {
        return plan;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }

    public long getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(long finishedAt) {
        this.finishedAt = finishedAt;
    }

    public FlightProgress getOrCreateCarrierProgress(String carrierId) {
        return progress.computeIfAbsent(carrierId, FlightProgress::new);
    }

    public Map<String, FlightProgress> getProgress() {
        return progress;
    }
}
