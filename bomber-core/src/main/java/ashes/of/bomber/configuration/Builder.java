package ashes.of.bomber.configuration;

import java.util.function.Supplier;

@FunctionalInterface
public interface Builder<T> {
    Supplier<T> build();
}
