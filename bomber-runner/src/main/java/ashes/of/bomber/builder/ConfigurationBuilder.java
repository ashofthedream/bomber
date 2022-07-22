package ashes.of.bomber.builder;

import ashes.of.bomber.annotations.Delay;
import ashes.of.bomber.annotations.LoadTestSettings;
import ashes.of.bomber.annotations.Throttle;
import ashes.of.bomber.configuration.Builder;
import ashes.of.bomber.configuration.Configuration;
import ashes.of.bomber.configuration.Settings;
import ashes.of.bomber.delayer.Delayer;
import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.squadron.Barrier;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigurationBuilder {

    private Builder<Supplier<Limiter>> limiter = LimiterBuilder::noLimit;
    private Builder<Supplier<Delayer>> delayer = DelayerBuilder::noDelay;
    private Builder<Barrier> barrier = BarrierBuilder::noBarrier;
    private Builder<Settings> settings = new SettingsBuilder();

    public ConfigurationBuilder(ConfigurationBuilder config) {
        this.barrier = config.barrier;
        this.delayer = config.delayer;
        this.limiter = config.limiter;
        this.settings = config.settings;
    }

    public ConfigurationBuilder() {
    }

    public ConfigurationBuilder settings(Builder<Settings> settings) {
        Objects.requireNonNull(settings, "settings is null");
        this.settings = settings;
        return this;
    }

    public ConfigurationBuilder settings(Consumer<SettingsBuilder> settings) {
        var current = this.settings.build();
        var builder = new SettingsBuilder()
                .setThreads(current.threads())
                .setIterations(current.iterations())
                .setDuration(current.duration());
        settings.accept(builder);

        this.settings = builder;
        return this;
    }

    public ConfigurationBuilder settings(@Nullable LoadTestSettings ann) {
        if (ann != null)
            settings(new SettingsBuilder()
                    .setTime(ann.time(), ann.timeUnit())
                    .setThreads(ann.threads())
                    .setIterations(ann.iterations())
            );

        return this;
    }


    public ConfigurationBuilder barrier(Builder<Barrier> barrier) {
        this.barrier = barrier;
        return this;
    }


    /**
     * Adds delayer which will be shared across all workers threads
     *
     * @param delayer shared limiter
     * @return builder
     */
    public ConfigurationBuilder delayer(Delayer delayer) {
        Objects.requireNonNull(delayer, "delayer is null");
        return delayer(() -> () -> delayer);
    }

    public ConfigurationBuilder delayer(Builder<Supplier<Delayer>> delayer) {
        this.delayer = delayer;
        return this;
    }

    public ConfigurationBuilder delayer(@Nullable Delay delay) {
        if (delay != null)
            delayer(new DelayerBuilder()
                    .min(delay.timeUnit().toMillis(delay.min()))
                    .max(delay.timeUnit().toMillis(delay.max()))
            );

        return this;
    }

    /**
     * Adds limiter which will be shared across all workers threads
     *
     * @param limiter shared limiter
     * @return builder
     */
    public ConfigurationBuilder limiter(Limiter limiter) {
        Objects.requireNonNull(limiter, "limiter is null");
        return limiter(() -> () -> limiter);
    }

    /**
     * Adds limiter which will be created for each worker thread
     * note: it may be shared if supplier will return same instance
     *
     * @param limiter shared request limiter
     * @return builder
     */
    public ConfigurationBuilder limiter(Builder<Supplier<Limiter>> limiter) {
        this.limiter = limiter;
        return this;
    }

    public ConfigurationBuilder limiter(@Nullable Throttle throttle) {
        if (throttle != null) {
            limiter(new LimiterBuilder()
                    .shared(throttle.shared())
                    .limit(throttle.threshold())
                    .time(throttle.time(), throttle.timeUnit()));
        }

        return this;
    }


    public ConfigurationBuilder process(Class<?> cls) {
        settings(cls.getAnnotation(LoadTestSettings.class));
        limiter(cls.getAnnotation(Throttle.class));
        delayer(cls.getAnnotation(Delay.class));
        return this;
    }

    public ConfigurationBuilder process(Method method) {
        settings(method.getAnnotation(LoadTestSettings.class));
        limiter(method.getAnnotation(Throttle.class));
        delayer(method.getAnnotation(Delay.class));

        return this;
    }


    public Configuration build() {
        return new Configuration(delayer.build(), limiter.build(), barrier.build(), settings.build());
    }
}
