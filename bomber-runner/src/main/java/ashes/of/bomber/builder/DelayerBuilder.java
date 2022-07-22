package ashes.of.bomber.builder;

import ashes.of.bomber.configuration.Builder;
import ashes.of.bomber.delayer.Delayer;
import ashes.of.bomber.delayer.NoDelayDelayer;
import ashes.of.bomber.delayer.RandomDelayer;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Supplier;

public class DelayerBuilder implements Builder<Delayer> {

    private boolean shared;
    private Duration min;
    private Duration max;


    public static Supplier<Delayer> noDelay() {
        return NoDelayDelayer::new;
    }

    public DelayerBuilder shared(boolean shared) {
        this.shared = true;
        return this;
    }

    public DelayerBuilder min(Duration duration) {
        this.min = duration;
        return this;
    }

    public DelayerBuilder min(long ms) {
        return min(Duration.ofMillis(ms));
    }


    public DelayerBuilder max(Duration duration) {
        this.max = duration;
        return this;
    }

    public DelayerBuilder max(long ms) {
        return max(Duration.ofMillis(ms));
    }


    private Supplier<Delayer> build(Duration min, Duration max) {
        Supplier<Delayer> builder = () -> new RandomDelayer(min, max);

        if (shared) {
            var delayer = builder.get();
            return () -> delayer;
        }

        return builder;
    }

    public Supplier<Delayer> build() {
        if (min == null && max == null)
            return NoDelayDelayer::new;

        Objects.requireNonNull(min, "min is null");
        Objects.requireNonNull(max, "max is null");

        return build(min, max);
    }
}
