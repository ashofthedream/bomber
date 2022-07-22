package ashes.of.bomber.delayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.locks.LockSupport;

public class RandomDelayer implements Delayer {
    private static final Logger log = LogManager.getLogger();

    private final Random random = new Random();
    private final long min;
    private final long max;

    public RandomDelayer(Duration min, Duration max) {
        if (min.toMillis() > max.toMillis())
            throw new RuntimeException("min delay time should be lower than max");

        this.min = min.toMillis();
        this.max = max.toMillis();
    }

    @Override
    public void delay() {
        long spread = max - min;
        long time = Math.round(min + spread * random.nextDouble());

        LockSupport.parkUntil(System.currentTimeMillis() + time);
    }
}
