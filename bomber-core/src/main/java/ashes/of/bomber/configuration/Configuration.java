package ashes.of.bomber.configuration;

import ashes.of.bomber.delayer.Delayer;
import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.squadron.Barrier;

import java.util.function.Supplier;

public record Configuration(Supplier<Delayer> delayer, Supplier<Limiter> limiter, Barrier barrier, Settings settings) {
}
