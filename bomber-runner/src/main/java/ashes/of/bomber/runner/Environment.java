package ashes.of.bomber.runner;

import ashes.of.bomber.core.limiter.Limiter;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.squadron.BarrierBuilder;
import ashes.of.bomber.watcher.Watcher;

import java.util.List;
import java.util.function.Supplier;

public class Environment {

    private final List<Sink> sinks;
    private final List<Watcher> watchers;
    private final Supplier<Limiter> limiter;
    private final BarrierBuilder barrier;

    public Environment(List<Sink> sinks, List<Watcher> watchers, Supplier<Limiter> limiter, BarrierBuilder barrier) {
        this.sinks = sinks;
        this.watchers = watchers;
        this.limiter = limiter;
        this.barrier = barrier;
    }

    public List<Sink> getSinks() {
        return sinks;
    }

    public List<Watcher> getWatchers() {
        return watchers;
    }

    public Supplier<Limiter> getLimiter() {
        return limiter;
    }

    public BarrierBuilder getBarrier() {
        return barrier;
    }
}
