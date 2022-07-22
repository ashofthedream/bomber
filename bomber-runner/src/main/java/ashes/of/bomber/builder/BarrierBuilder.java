package ashes.of.bomber.builder;

import ashes.of.bomber.configuration.Builder;
import ashes.of.bomber.squadron.Barrier;
import ashes.of.bomber.squadron.LocalCascadeBarrier;
import ashes.of.bomber.squadron.NoBarrier;

public class BarrierBuilder implements Builder<Barrier> {

    private boolean enabled;

    public static Barrier noBarrier() {
        return new NoBarrier();
    }

    public BarrierBuilder enabled(boolean enabled) {
        this.enabled = true;
        return this;
    }

    @Override
    public Barrier build() {
        return enabled ? new LocalCascadeBarrier() : new NoBarrier();
    }
}
