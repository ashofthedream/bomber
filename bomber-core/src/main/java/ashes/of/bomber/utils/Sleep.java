package ashes.of.bomber.utils;

import java.util.Random;

public class Sleep {
    private static final Random random = new Random();

    public static void sleepQuietlyExact(long ms) {
        sleepQuitly(ms, 0.0);
    }

    public static void sleepQuietlyAround(long ms) {
        sleepQuitly(ms, 0.2);
    }

    private static void sleepQuitly(long ms, double sp) {
        try {
            double spread = ms * Math.max(0.0, Math.min(sp, 1.0));
            double timeout = ms + random.nextDouble() * spread * 2 - spread;

            Thread.sleep(Math.round(timeout));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
