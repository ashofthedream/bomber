package ashes.of.bomber.carrier.dto.events;

import ashes.of.bomber.carrier.dto.flight.AppStateDto;
import ashes.of.bomber.configuration.Stage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class SinkEvent {
    private static final AtomicLong eventIdSeq = new AtomicLong();

    private long id;
    private long timestamp;
    private SinkEventType type;
    private long flightId;
    private String carrierId;


    @Nullable
    private String testApp;

    @Nullable
    private String testSuite;

    @Nullable
    private String testCase;

    @Nullable
    private Stage stage;

    private AppStateDto state;
    private List<HistogramPointDto> histograms = new ArrayList<>();

    public static long nextId() {
        return eventIdSeq.incrementAndGet();
    }

    public long getId() {
        return id;
    }

    public SinkEvent setId(long id) {
        this.id = id;
        return this;
    }

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

    public Stage getStage() {
        return stage;
    }

    public SinkEvent setStage(Stage stage) {
        this.stage = stage;
        return this;
    }

    @Nullable
    public String getTestApp() {
        return testApp;
    }

    public SinkEvent setTestApp(@Nullable String testApp) {
        this.testApp = testApp;
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

    public AppStateDto getState() {
        return state;
    }

    public SinkEvent setState(AppStateDto state) {
        this.state = state;
        return this;
    }

    public List<HistogramPointDto> getHistograms() {
        return histograms;
    }

    public SinkEvent setHistograms(List<HistogramPointDto> histograms) {
        this.histograms = histograms;
        return this;
    }

    @Override
    public String toString() {
        return "SinkEvent{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", type=" + type +
                ", flightId=" + flightId +
                ", carrierId='" + carrierId + '\'' +
                ", stage='" + stage + '\'' +
                ", testSuite='" + testSuite + '\'' +
                ", testCase='" + testCase + '\'' +
                ", state=" + state +
                '}';
    }
}
