package ashes.of.trebuchet.manager.utils.random;

import java.util.Random;


public class DefaultNonSecureGenerator implements RandomGenerator {
    private final Random random = new Random();


    @Override
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    @Override
    public long nextLong(long bound) {
        return Math.round(nextDouble(bound));
    }

    @Override
    public double nextDouble(double bound) {
        return random.nextDouble() * bound;
    }

    @Override
    public double nextDouble() {
        return random.nextDouble();
    }
}
