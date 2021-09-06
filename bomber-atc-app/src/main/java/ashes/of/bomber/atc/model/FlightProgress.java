package ashes.of.bomber.atc.model;

import ashes.of.bomber.carrier.dto.ApplicationStateDto;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class FlightProgress {
    private final String carrierId;
    private final List<FlightRecord> records = new CopyOnWriteArrayList<>();

    public FlightProgress(String carrierId) {
        this.carrierId = carrierId;
    }


    public void add(FlightRecord record) {
        FlightRecord last = getActual();
//        if (last != null && isSameProgressEvent(record, last)) {
//            records.set(records.size() - 1, record);
//            return;
//        }

        records.add(record);
    }

    public void add(ApplicationStateDto state) {
        long now = System.currentTimeMillis();

        FlightRecord record = new FlightRecord("TEST_CASE_PROGRESS", now, state);
        record.setTestSuite(state.getTestSuite());
        record.setTestCase(state.getTestCase());

        add(record);
    }

    private boolean isSameProgressEvent(FlightRecord record, FlightRecord last) {
        return Objects.equals(last.getType(), "TEST_CASE_PROGRESS") &&
                Objects.equals(last.getTestSuite(), record.getTestSuite()) &&
                Objects.equals(last.getTestCase(), record.getTestCase());
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
