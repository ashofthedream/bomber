package ashes.of.bomber.carrier.dto.flight;

public class TestCaseSnapshotDto {
    private String name;
    private SettingsDto settings;
    private long startTime;
    private Long finishTime;

    private long currentIterationsCount;
    private long errorsCount;

    public String getName() {
        return name;
    }

    public TestCaseSnapshotDto setName(String name) {
        this.name = name;
        return this;
    }

    public SettingsDto getSettings() {
        return settings;
    }

    public TestCaseSnapshotDto setSettings(SettingsDto settings) {
        this.settings = settings;
        return this;
    }

    public long getStartTime() {
        return startTime;
    }

    public TestCaseSnapshotDto setStartTime(long startTime) {
        this.startTime = startTime;
        return this;
    }

    public Long getFinishTime() {
        return finishTime;
    }

    public TestCaseSnapshotDto setFinishTime(Long finishTime) {
        this.finishTime = finishTime;
        return this;
    }

    public long getCurrentIterationsCount() {
        return currentIterationsCount;
    }

    public TestCaseSnapshotDto setCurrentIterationsCount(long currentIterationsCount) {
        this.currentIterationsCount = currentIterationsCount;
        return this;
    }

    public long getErrorsCount() {
        return errorsCount;
    }

    public TestCaseSnapshotDto setErrorsCount(long errorsCount) {
        this.errorsCount = errorsCount;
        return this;
    }

}
