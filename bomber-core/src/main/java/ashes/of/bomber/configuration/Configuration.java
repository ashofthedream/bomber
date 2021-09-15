package ashes.of.bomber.configuration;

import ashes.of.bomber.delayer.Delayer;
import ashes.of.bomber.delayer.DelayerBuilder;
import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.limiter.LimiterBuilder;
import ashes.of.bomber.squadron.Barrier;
import ashes.of.bomber.squadron.BarrierBuilder;

import java.util.function.Supplier;


public class Configuration {

    private final DelayerBuilder delayer;
    private final LimiterBuilder limiter;
    private final BarrierBuilder barrier;
    private final Settings warmUp;
    private final Settings settings;

    public Configuration(DelayerBuilder delayer, LimiterBuilder limiter, BarrierBuilder barrier, Settings warmUp, Settings settings) {
        this.delayer = delayer;
        this.limiter = limiter;
        this.barrier = barrier;
        this.warmUp = warmUp;
        this.settings = settings;
    }

    public DelayerBuilder getDelayer() {
        return delayer;
    }

    public LimiterBuilder getLimiter() {
        return limiter;
    }

    public BarrierBuilder getBarrier() {
        return barrier;
    }

    public Settings getWarmUp() {
        return warmUp;
    }

    public Settings getSettings() {
        return settings;
    }
}
