package ashes.of.bomber.atc.utils;

import ashes.of.bomber.atc.utils.random.RandomGenerator;
import com.google.common.primitives.Doubles;

import java.util.Objects;


public class Weighed<T> implements Comparable<Weighed<T>> {

    private final T value;
    private final double weight;


    public Weighed(T value, double weight) {
        this.value = Objects.requireNonNull(value,"value is null");
        this.weight = weight;
    }

    public static <T> Weighed<T> random(T value) {
        return new Weighed<>(value, RandomGenerator.shared().nextDouble(1.0));
    }


    public T getValue() {
        return value;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public int compareTo(Weighed<T> w) {
        return Doubles.compare(w.weight, weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Weighed<?> weighed = (Weighed<?>) o;

        return Double.compare(weighed.weight, weight) == 0;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(weight);
        return (int) (temp ^ (temp >>> 32));
    }

    @Override
    public String toString() {
        return "Weighed{" +
                "value=" + value +
                ", weight=" + weight +
                '}';
    }
}
