package ashes.of.bomber.snapshots;

import ashes.of.bomber.configuration.Settings;
import ashes.of.bomber.configuration.Stage;

import java.time.Instant;
import java.util.List;

public class FlightSnapshot {
    private final Stage stage;
    private final Settings settings;
    private final String testSuite;
    private final String testCase;

    private final long iterationsCount;
    private final long remainIterationsCount;
    private final long errorCount;

    private final Instant testSuiteStartTime;
    private final Instant testCaseStartTime;
    private final long caseElapsedTime;
    private final long caseRemainTime;
    private final List<WorkerSnapshot> workers;


    public FlightSnapshot(Stage stage, Settings settings, String testSuite, String testCase,
                          long iterationsCount, long remainIterationsCount, long errorCount,
                          Instant testSuiteStartTime, Instant testCaseStartTime,
                          long caseElapsedTime, long caseRemainTime,
                          List<WorkerSnapshot> workers) {
        this.stage = stage;
        this.settings = settings;
        this.testSuite = testSuite;
        this.testCase = testCase;
        this.iterationsCount = iterationsCount;
        this.remainIterationsCount = remainIterationsCount;
        this.errorCount = errorCount;
        this.testSuiteStartTime = testSuiteStartTime;
        this.testCaseStartTime = testCaseStartTime;
        this.caseElapsedTime = caseElapsedTime;
        this.caseRemainTime = caseRemainTime;
        this.workers = workers;
    }

    public Stage getStage() {
        return stage;
    }

    public Settings getSettings() {
        return settings;
    }

    public String getTestSuite() {
        return testSuite;
    }

    public String getTestCase() {
        return testCase;
    }

    public long getIterationsCount() {
        return iterationsCount;
    }

    public long getRemainIterationsCount() {
        return remainIterationsCount;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public Instant getTestSuiteStartTime() {
        return testSuiteStartTime;
    }

    public Instant getTestCaseStartTime() {
        return testCaseStartTime;
    }

    public long getCaseElapsedTime() {
        return caseElapsedTime;
    }

    public long getCaseRemainTime() {
        return caseRemainTime;
    }

    public List<WorkerSnapshot> getWorkers() {
        return workers;
    }
}
