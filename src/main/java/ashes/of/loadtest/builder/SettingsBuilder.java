package ashes.of.loadtest.builder;

import ashes.of.loadtest.settings.Settings;

import java.time.Duration;
import java.util.function.Consumer;


public class SettingsBuilder {

    private Settings baseline = new Settings()
            .time(Duration.ofSeconds(30));

    private Settings warmUp = new Settings()
            .threads(1)
            .time(Duration.ofSeconds(30));

    private Settings test = new Settings();

    public SettingsBuilder copyOf(SettingsBuilder builder) {
        return baseline(builder.baseline)
                .warmUp(builder.warmUp)
                .test(builder.test);
    }

    public SettingsBuilder baseline(Settings settings) {
        this.baseline = new Settings(settings);
        return this;
    }

    public SettingsBuilder baseline(Consumer<Settings> consumer) {
        consumer.accept(baseline);
        return this;
    }


    public SettingsBuilder warmUp(Settings settings) {
        this.warmUp = new Settings(settings);
        return this;
    }

    public SettingsBuilder warmUp(Consumer<Settings> consumer) {
        consumer.accept(warmUp);
        return this;
    }


    public SettingsBuilder test(Settings settings) {
        this.test = new Settings(settings);
        return this;
    }

    public SettingsBuilder test(Consumer<Settings> consumer) {
        consumer.accept(test);
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
