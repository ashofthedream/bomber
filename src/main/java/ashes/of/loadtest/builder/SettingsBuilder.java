package ashes.of.loadtest.builder;

import ashes.of.loadtest.settings.Settings;

import java.time.Duration;
import java.util.function.Consumer;


public class SettingsBuilder {

    private Settings baseline = new Settings()
            .time(Duration.ofSeconds(30));

    private Settings warmUp = new Settings()
            .threadCount(1)
            .time(Duration.ofSeconds(30));

    private Settings test = new Settings();


    public SettingsBuilder baseline(Settings settings) {
        this.baseline = settings;
        return this;
    }

    public SettingsBuilder baseline(Consumer<Settings> settings) {
        settings.accept(baseline);
        return this;
    }


    public SettingsBuilder warmUp(Settings settings) {
        this.warmUp = settings;
        return this;
    }

    public SettingsBuilder warmUp(Consumer<Settings> settings) {
        settings.accept(warmUp);
        return this;
    }


    public SettingsBuilder test(Settings settings) {
        this.test = settings;
        return this;
    }

    public SettingsBuilder test(Consumer<Settings> settings) {
        settings.accept(test);
        return this;
    }


    public Settings getBaseline() {
        return baseline;
    }

    public Settings getWarmUp() {
        return warmUp;
    }

    public Settings getTest() {
        return test;
    }
}
