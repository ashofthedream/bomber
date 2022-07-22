package ashes.of.bomber.builder;

import ashes.of.bomber.configuration.Builder;
import ashes.of.bomber.squadron.Barrier;
import ashes.of.bomber.squadron.LocalCascadeBarrier;
import ashes.of.bomber.squadron.NoBarrier;

import java.util.function.Supplier;

public class BarrierBuilder implements Builder<Barrier> {

    private boolean enabled;

    public static Supplier<Barrier> noBarrier() {
        return NoBarrier::new;
    }

    public BarrierBuilder enabled(boolean enabled) {
        this.enabled = true;
        return this;
    }

    @Override
    public Supplier<Barrier> build() {
        return enabled ? LocalCascadeBarrier::new : NoBarrier::new;
    }
}
