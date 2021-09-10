package ashes.of.bomber.builder;

import ashes.of.bomber.annotations.Delay;
import ashes.of.bomber.annotations.LoadTest;
import ashes.of.bomber.annotations.Throttle;
import ashes.of.bomber.annotations.WarmUp;
import ashes.of.bomber.delayer.Delayer;
import ashes.of.bomber.delayer.NoDelayDelayer;
import ashes.of.bomber.delayer.RandomDelayer;
import ashes.of.bomber.configuration.Settings;
import ashes.of.bomber.configuration.SettingsBuilder;
import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.configuration.Configuration;
import ashes.of.bomber.squadron.BarrierBuilder;
import ashes.of.bomber.squadron.NoBarrier;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class ConfigurationBuilder {

    private BarrierBuilder barrier = new NoBarrier.Builder();
    private Supplier<Delayer> delayer = NoDelayDelayer::new;
    private Supplier<Limiter> limiter = Limiter::alwaysPermit;
    private Settings warmUp = SettingsBuilder.disabled();
    private Settings settings = SettingsBuilder.disabled();

    public ConfigurationBuilder(ConfigurationBuilder config) {
        this.barrier = config.barrier;
        this.delayer = config.delayer;
        this.limiter = config.limiter;
        this.warmUp = config.warmUp;
        this.settings = config.settings;
    }

    public ConfigurationBuilder() {
    }

    public ConfigurationBuilder warmUp(Settings settings) {
        Objects.requireNonNull(settings, "settings is null");
        this.warmUp = settings;
        return this;
    }

    public ConfigurationBuilder warmUp(Consumer<Settings> settings) {
        settings.accept(warmUp);
        return this;
    }

    public ConfigurationBuilder warmUp(@Nullable WarmUp ann) {
        if (ann != null)
            warmUp(new SettingsBuilder()
                .setTime(ann.time(), ann.timeUnit())
                .setThreadsCount(ann.threads())
                .setThreadIterationsCount(ann.threadIterations())
                .setTotalIterationsCount(ann.totalIterations())
                .build());

        return this;
    }


    public ConfigurationBuilder settings(Settings settings) {
        Objects.requireNonNull(settings, "settings is null");
        this.settings = settings;
        return this;
    }

    public ConfigurationBuilder settings(Consumer<SettingsBuilder> settings) {
        SettingsBuilder builder = new SettingsBuilder();
        settings.accept(builder);
        return settings(builder.build());
    }

    public ConfigurationBuilder settings(@Nullable LoadTest ann) {
        if (ann != null)
            settings(new SettingsBuilder()
                    .setTime(ann.time(), ann.timeUnit())
                    .setThreadsCount(ann.threads())
                    .setThreadIterationsCount(ann.threadIterations())
                    .setTotalIterationsCount(ann.totalIterations())
                    .build());

        return this;
    }


    public ConfigurationBuilder barrier(BarrierBuilder barrier) {
        this.barrier = barrier;
        return this;
    }


    public ConfigurationBuilder delayer(Delayer delayer) {
        Objects.requireNonNull(delayer, "delayer is null");
        return delayer(() -> delayer);
    }

    public ConfigurationBuilder delayer(Supplier<Delayer> delayer) {
        this.delayer = delayer;
        return this;
    }

    public ConfigurationBuilder delayer(@Nullable Delay delay) {
        if (delay != null)
            delayer(new RandomDelayer(delay.min(), delay.max(), delay.timeUnit()));

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
        return limiter(() -> limiter);
    }

    /**
     * Adds limiter which will be created for each worker thread
     * note: it may be shared if supplier will return same instance
     *
     * @param limiter shared request limiter
     * @return builder
     */
    public ConfigurationBuilder limiter(Supplier<Limiter> limiter) {
        this.limiter = limiter;
        return this;
    }

    public ConfigurationBuilder limiter(@Nullable Throttle throttle) {
        if (throttle != null) {
            Supplier<Limiter> limiter = () -> Limiter.withRate(throttle.threshold(), throttle.time(), throttle.timeUnit());
            if (throttle.shared()) {
                limiter(limiter.get());
            } else {
                limiter(limiter);
            }
        }
        return this;
    }

    public ConfigurationBuilder process(Class<?> cls) {
        warmUp(cls.getAnnotation(WarmUp.class));
        settings(cls.getAnnotation(LoadTest.class));
        limiter(cls.getAnnotation(Throttle.class));
        delayer(cls.getAnnotation(Delay.class));
        return this;
    }

    public ConfigurationBuilder process(Method method) {
        warmUp(method.getAnnotation(WarmUp.class));
        settings(method.getAnnotation(LoadTest.class));
        limiter(method.getAnnotation(Throttle.class));
        delayer(method.getAnnotation(Delay.class));

        return this;
    }

    public Configuration build() {
        return new Configuration(delayer, limiter, barrier, warmUp, settings);
    }
}
