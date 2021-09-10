package ashes.of.bomber.configuration;

import ashes.of.bomber.delayer.Delayer;
import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.squadron.BarrierBuilder;

import java.util.function.Supplier;


public class Configuration {

    private final Supplier<Delayer> delayer;
    private final Supplier<Limiter> limiter;
    private final BarrierBuilder barrier;
    private final Settings warmUp;
    private final Settings settings;

    public Configuration(Supplier<Delayer> delayer, Supplier<Limiter> limiter, BarrierBuilder barrier, Settings warmUp, Settings settings) {
        this.delayer = delayer;
        this.limiter = limiter;
        this.barrier = barrier;
        this.warmUp = warmUp;
        this.settings = settings;
    }

    public Supplier<Delayer> getDelayer() {
        return delayer;
    }

    public Supplier<Limiter> getLimiter() {
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
