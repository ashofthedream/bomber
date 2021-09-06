package ashes.of.bomber.carrier.dto;

public class TestCaseDto {
    private String name;
    private SettingsDto loadTest;
    private SettingsDto warmUp;

    public String getName() {
        return name;
    }

    public TestCaseDto setName(String name) {
        this.name = name;
        return this;
    }

    public SettingsDto getLoadTest() {
        return loadTest;
    }

    public TestCaseDto setLoadTest(SettingsDto loadTest) {
        this.loadTest = loadTest;
        return this;
    }

    public SettingsDto getWarmUp() {
        return warmUp;
    }

    public TestCaseDto setWarmUp(SettingsDto warmUp) {
        this.warmUp = warmUp;
        return this;
    }
}
