package ashes.of.bomber.carrier.dto.events;

import javax.annotation.Nullable;

public class SinkEvent {
    private long timestamp;
    private SinkEventType type;
    private long flightId;
    private String carrierId;

    private String stage;

    @Nullable
    private String testSuite;

    @Nullable
    private String testCase;

    public long getTimestamp() {
        return timestamp;
    }

    public SinkEvent setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public SinkEventType getType() {
        return type;
    }

    public SinkEvent setType(SinkEventType type) {
        this.type = type;
        return this;
    }

    public long getFlightId() {
        return flightId;
    }

    public SinkEvent setFlightId(long flightId) {
        this.flightId = flightId;
        return this;
    }

    public String getCarrierId() {
        return carrierId;
    }

    public SinkEvent setCarrierId(String carrierId) {
        this.carrierId = carrierId;
        return this;
    }

    public String getStage() {
        return stage;
    }

    public SinkEvent setStage(String stage) {
        this.stage = stage;
        return this;
    }

    @Nullable
    public String getTestSuite() {
        return testSuite;
    }

    public SinkEvent setTestSuite(@Nullable String testSuite) {
        this.testSuite = testSuite;
        return this;
    }

    @Nullable
    public String getTestCase() {
        return testCase;
    }

    public SinkEvent setTestCase(@Nullable String testCase) {
        this.testCase = testCase;
        return this;
    }
}
