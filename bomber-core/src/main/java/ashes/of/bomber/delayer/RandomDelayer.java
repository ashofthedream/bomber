package ashes.of.bomber.delayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RandomDelayer implements Delayer {
    private static final Logger log = LogManager.getLogger();

    private final Random random = new Random();
    private final long min;
    private final long max;

    public RandomDelayer(long min, long max, TimeUnit timeUnit) {
        if (min > max)
            throw new RuntimeException("min delay time should be lower than max");
        this.min = timeUnit.toMillis(min);
        this.max = timeUnit.toMillis(max);
    }

    @Override
    public void delay() {
        long spread = max - min;
        long time = Math.round(min + spread * random.nextDouble());
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            log.trace("We've been interrupted");
        }
    }
}
