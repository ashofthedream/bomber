package ashes.of.bomber.builder;

import ashes.of.bomber.core.limiter.Limiter;
import ashes.of.bomber.sink.Sink;
import ashes.of.bomber.squadron.BarrierBuilder;
import ashes.of.bomber.squadron.NoBarrier;
import ashes.of.bomber.watcher.Watcher;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class EnvironmentBuilder {
    protected final List<Sink> sinks = new ArrayList<>();
    protected final List<Watcher> watchers = new ArrayList<>();
    protected final SettingsBuilder settings = new SettingsBuilder();
    protected Supplier<Limiter> limiter = Limiter::alwaysPermit;
    protected BarrierBuilder barrier = new NoBarrier.Builder();
}
