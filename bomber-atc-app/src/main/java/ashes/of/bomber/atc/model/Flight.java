package ashes.of.bomber.atc.model;

import ashes.of.bomber.carrier.dto.events.SinkEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Flight {
    private final long id;

    private final Map<String, FlightProgress> progress = new ConcurrentHashMap<>();
    private volatile long startedAt;
    private volatile long finishedAt;

    public Flight(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
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

    public FlightProgress getData(String carrierId) {
        return progress.computeIfAbsent(carrierId, FlightProgress::new);
    }

    public Map<String, FlightProgress> getProgress() {
        return progress;
    }

    public void event(SinkEvent event) {
        FlightProgress data = getData(event.getCarrierId());

        FlightRecord record = new FlightRecord(event.getType().name(), event.getTimestamp(), null);
        record.setTestSuite(event.getTestSuite());
        record.setTestCase(event.getTestCase());
        record.setState(event.getState());

        data.add(record);
    }
}
