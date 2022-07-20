package ashes.of.bomber.configuration;

import ashes.of.bomber.delayer.DelayerBuilder;
import ashes.of.bomber.limiter.LimiterBuilder;
import ashes.of.bomber.squadron.BarrierBuilder;

public record Configuration(DelayerBuilder delayer, LimiterBuilder limiter, BarrierBuilder barrier, Settings settings) {

}
