package ashes.of.bomber.example.utils;

import java.util.Random;

public class RandomUtils {
    private static final Random random = new Random();

    public static boolean withProbability(double probability) {
        return random.nextDouble() < probability;
    }
}
