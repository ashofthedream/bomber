package ashes.of.bomber.configuration;

import ashes.of.bomber.delayer.DelayerBuilder;
import ashes.of.bomber.limiter.LimiterBuilder;
import ashes.of.bomber.squadron.BarrierBuilder;


public class Configuration {

    private final DelayerBuilder delayer;
    private final LimiterBuilder limiter;
    private final BarrierBuilder barrier;
    private final Settings settings;

    public Configuration(DelayerBuilder delayer, LimiterBuilder limiter, BarrierBuilder barrier, Settings settings) {
        this.delayer = delayer;
        this.limiter = limiter;
        this.barrier = barrier;
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

    public Settings getSettings() {
        return settings;
    }
}
