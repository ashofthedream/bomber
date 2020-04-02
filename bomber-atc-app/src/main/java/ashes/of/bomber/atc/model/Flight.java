package ashes.of.bomber.atc.model;

import ashes.of.bomber.carrier.dto.ApplicationStateDto;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Flight {
    private final long id;

    private final Map<String, FlightData> data = new ConcurrentHashMap<>();
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

    public FlightData getData(String carrierId) {
        return data.computeIfAbsent(carrierId, FlightData::new);
    }

    public Map<String, FlightData> getData() {
        return data;
    }


    public static class FlightRecord {
        private String type;
        private long timestamp;

        private String testSuite;
        private String testCase;

        private ApplicationStateDto state;

        public FlightRecord(String type, long timestamp, ApplicationStateDto state) {
            this.type = type;
            this.timestamp = timestamp;
            this.state = state;
        }

        public String getType() {
            return type;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getTestSuite() {
            return testSuite;
        }

        public String getTestCase() {
            return testCase;
        }

        public ApplicationStateDto getState() {
            return state;
        }
    }

    public static class FlightData {
        private final String carrierId;

        private List<FlightRecord> records = new CopyOnWriteArrayList<>();
        private FlightData(String carrierId) {
            this.carrierId = carrierId;
        }

        public void add(ApplicationStateDto state) {
            long now = System.currentTimeMillis();

            if ("Idle".equals(state.getStage()))
                return;

            Optional<FlightRecord> appStarted = records.stream()
                    .filter(record -> Objects.equals(record.type, "START_APP"))
                    .findAny();

            if (!appStarted.isPresent()) {
                records.add(new FlightRecord("START_APP", now, state));
            }

            Optional<FlightRecord> testSuiteStarted = records.stream()
                    .filter(record ->
                            Objects.equals(record.type, "START_TEST_SUITE") &&
                            Objects.equals(record.testSuite, state.getTestSuite())
                    )
                    .findAny();

            if (!testSuiteStarted.isPresent()) {
                FlightRecord record = new FlightRecord("START_TEST_SUITE", now, state);
                record.testSuite = state.getTestSuite();
                records.add(record);
            }

            Optional<FlightRecord> testCaseStarted = records.stream()
                    .filter(record ->
                            Objects.equals(record.type, "START_TEST_CASE") &&
                            Objects.equals(record.testSuite, state.getTestSuite()) &&
                            Objects.equals(record.testCase, state.getTestCase())
                    )
                    .findAny();

            if (!testCaseStarted.isPresent()) {
                FlightRecord record = new FlightRecord("START_TEST_CASE", now, state);
                record.testSuite = state.getTestSuite();
                record.testCase = state.getTestCase();
                records.add(record);
            }

            FlightRecord record = new FlightRecord("TEST_CASE_PROGRESS", now, state);
            record.testSuite = state.getTestSuite();
            record.testCase = state.getTestCase();

            FlightRecord actual = getActual();
            if (actual != null && Objects.equals(actual.getType(), "TEST_CASE_PROGRESS")) {
                records.set(records.size() - 1, record);
                return;
            }

            records.add(record);
        }

        public List<FlightRecord> getRecords() {
            return records;
        }

        @Nullable
        public FlightRecord getActual() {
            int last = records.size() - 1;
            if (last < 0)
                return null;

            return records.get(last);
        }
    }
}
