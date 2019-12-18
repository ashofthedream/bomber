package ashes.of.bomber.atc.utils.random;


import ashes.of.bomber.atc.utils.ArrayUtils;
import ashes.of.bomber.atc.utils.Weighed;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;


public interface RandomGenerator {

    RandomGenerator shared = new DefaultNonSecureGenerator();

    static RandomGenerator shared() {
        return shared;
    }


    int nextInt(int bound);

    default int nextInt() {
        return nextInt(Integer.MAX_VALUE);
    }

    default int nextInt(int start, int end) {
        return nextInt(end - start) + start;
    }


    long nextLong(long bound);

    default long nextLong() {
        return nextLong(Long.MAX_VALUE);
    }

    default long nextLong(long start, long end) {
        return nextLong(end - start) + start;
    }


    double nextDouble(double bound);

    default double nextDouble() {
        return nextDouble(1.0);
    }

    default double nextDouble(double start, double end) {
        return nextDouble(end - start) + start;
    }


    default boolean withProbability(double p) {
        return nextDouble(1.0) < p;
    }


    /**
     * Returns any element from array
     *
     * @param a array
     * @return any element
     */
    default<T> T any(T... a) {
        return a[nextInt(a.length)];
    }


    default long random(long... a) {
        return a[nextInt(a.length)];
    }

    default int random(int... a) {
        return a[nextInt(a.length)];
    }

    default double random(double... a) {
        return a[nextInt(a.length)];
    }


    /**
     * Returns any element from collection
     *
     * @param collection collection
     * @return random element from collection
     */
    default<T> Optional<T> random(Collection<T> collection) {
        if (!collection.isEmpty()) {
            int i = nextInt(collection.size());

            for (T el : collection)
                if (--i < 0)
                    return Optional.of(el);
        }

        return Optional.empty();
    }

    default<T> T randomOrDefault(Collection<T> c, T def) {
        return random(c).orElse(def);
    }

    @Nullable
    default<T> T randomOrNull(Collection<T> c) {
        return random(c).orElse(null);
    }


    /**
     * Returns any element from list
     *
     * @param list list with elements
     * @return any element
     */
    default<T> Optional<T> random(List<T> list) {
        if (list.isEmpty())
            return Optional.empty();

        return Optional.of(list.size())
                .map(this::nextInt)
                .map(list::get);
    }

    default<T> T randomOrDefault(List<T> list, T def) {
        return random(list).orElse(def);
    }

    @Nullable
    default<T> T randomOrNull(List<T> list) {
        return random(list).orElse(null);
    }


    /**
     * Returns any key from map
     *
     * @param m map
     * @return random key from map
     */
    default<K, V> Optional<K> randomKey(Map<K, V> m) {
        return random(m.keySet());
    }

    /**
     * Returns any key from map
     *
     * @param m map
     * @return random key from map or default value
     */
    default<K, V> K randomKeyOrDefault(Map<K, V> m, K def) {
        return random(m.keySet()).orElse(def);
    }

    /**
     * Returns any key from map
     *
     * @param m map
     * @return random key from map or null
     */
    @Nullable
    default<K, V> K randomKeyOrNull(Map<K, V> m) {
        return random(m.keySet()).orElse(null);
    }


    /**
     * Returns any value from map
     *
     * @param m map
     * @return random value from map
     */
    default<K, V> Optional<V> randomValue(Map<K, V> m) {
        return randomKey(m).map(m::get);
    }

    /**
     * Returns any value from map
     *
     * @param m map
     * @return random value from map or default value
     */
    default<K, V> V randomValueOrDefault(Map<K, V> m, V def) {
        return randomValue(m).orElse(def);
    }

    /**
     * Returns any value from map
     *
     * @param m map
     * @return random value from map or default value
     */
    @Nullable
    default<K, V> V randomValueOrNull(Map<K, V> m) {
        return randomValue(m).orElse(null);
    }


    /**
     * Returns any entry from map
     *
     * @param m map
     * @return random entry from map
     */
    default<K, V> Optional<Map.Entry<K, V>> randomEntry(Map<K, V> m) {
        return random(m.entrySet());
    }


    default<T> Optional<T> randomByWeight(Collection<Weighed<T>> collection) {
        double sum = 0.0;
        for (Weighed<T> weighed : collection)
            sum += weighed.getWeight();

        double random = nextDouble(sum);
        double prob = 0.0;
        for (Weighed<T> w : collection) {
            prob += w.getWeight();
            if (random < prob)
                return Optional.of(w.getValue());
        }

        return Optional.empty();
    }

    default<T> T randomByWeightOrDefault(Collection<Weighed<T>> collection, T def) {
        return randomByWeight(collection).orElse(def);
    }


    default<K> Optional<K> randomKeyByWeight(Map<K, Double> map) {
        List<Weighed<K>> list = map.entrySet().stream()
                .map(e -> new Weighed<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        return randomByWeight(list);
    }

    default<T> T randomKeyByWeightOrDefault(Map<T, Double> map, T def) {
        return randomKeyByWeight(map).orElse(def);
    }


    default int[] shuffle(int... array) {
        for (int i = 0; i < array.length; i++)
            ArrayUtils.swap(i, nextInt(i + 1), array);

        return array;
    }

    default long[] shuffle(long... array) {
        for (int i = 0; i < array.length; i++)
            ArrayUtils.swap(i, nextInt(i + 1), array);

        return array;
    }

    default double[] shuffle(double... array) {
        for (int i = 0; i < array.length; i++)
            ArrayUtils.swap(i, nextInt(i + 1), array);

        return array;
    }


    default<T> T[] shuffle(T... array) {
        for (int i = 0; i < array.length; i++)
            ArrayUtils.swap(i, nextInt(i + 1), array);

        return array;
    }


    default<T> List<T> shuffle(List<T> list) {
        for (int i = 0; i < list.size(); i++)
            Collections.swap(list, i, nextInt(i + 1));

        return list;
    }
}
