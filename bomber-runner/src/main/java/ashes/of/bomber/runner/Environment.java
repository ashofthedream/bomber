package ashes.of.bomber.runner;

import ashes.of.bomber.delayer.Delayer;
import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.squadron.BarrierBuilder;
import ashes.of.bomber.watcher.WatcherConfig;

import java.util.List;
import java.util.function.Supplier;


public class Environment {

    private final List<Sink> sinks;
    private final List<WatcherConfig> watchers;
    private final Delayer delayer;
    private final Supplier<Limiter> limiter;
    private final BarrierBuilder barrier;

    public Environment(List<Sink> sinks, List<WatcherConfig> watchers, Delayer delayer, Supplier<Limiter> limiter, BarrierBuilder barrier) {
        this.sinks = sinks;
        this.watchers = watchers;
        this.delayer = delayer;
        this.limiter = limiter;
        this.barrier = barrier;
    }

    public List<Sink> getSinks() {
        return sinks;
    }

    public List<WatcherConfig> getWatchers() {
        return watchers;
    }

    public Delayer getDelayer() {
        return delayer;
    }

    public Supplier<Limiter> getLimiter() {
        return limiter;
    }

    public BarrierBuilder getBarrier() {
        return barrier;
    }
}
