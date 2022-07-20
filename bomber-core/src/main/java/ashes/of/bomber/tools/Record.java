package ashes.of.bomber.tools;

import ashes.of.bomber.flight.Iteration;

import javax.annotation.Nullable;

public record Record(Iteration iteration, String label, long timestamp, long elapsed, boolean success, @Nullable Throwable error) {
}
