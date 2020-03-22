package ashes.of.bomber.example.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class SleepUtils {
    private static final Logger log = LogManager.getLogger();

    private static final Random random = new Random();

    public static void sleepQuietlyExact(long ms) {
        sleepQuietly(ms, 0.0);
    }

    public static void sleepQuietlyAround(long ms) {
        sleepQuietly(ms, 0.2);
    }

    public static void sleepQuietly(long ms, double sp) {
        try {
            double spread = ms * Math.max(0.0, Math.min(sp, 1.0));
            double timeout = ms + random.nextDouble() * spread * 2 - spread;

            log.trace("timeout: {}, spread: {}", Math.round(timeout), spread);
            Thread.sleep(Math.round(timeout));
        } catch (InterruptedException e) {
            log.error("interrupted", e);
        }
    }
}
