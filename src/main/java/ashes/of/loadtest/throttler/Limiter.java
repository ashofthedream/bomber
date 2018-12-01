package ashes.of.loadtest.throttler;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


public interface Limiter {

    /**
     * @see this#waitForAcquire(long)
     */
    default boolean waitForAcquire(Duration duration) {
        return waitForAcquire(duration.toMillis());
    }

    /**
     * @see this#waitForAcquire(long)
     */
    default boolean waitForAcquire(long time, TimeUnit unit) {
        return waitForAcquire(unit.toMillis(time));
    }


    boolean waitForAcquire(long ms);

    boolean waitForAcquire();

    boolean tryAcquire();


    static Limiter withRate(int count, Duration duration) {
        return new RateLimiter(count, duration);
    }

    static Limiter withRate(int count, long millis) {
        return withRate(count, Duration.ofMillis(millis));
    }

    static Limiter withRate(int count, long time, TimeUnit unit) {
        return withRate(count, unit.toMillis(time));
    }


    static Limiter alwaysPermit() {
        return new OneAnswerLimiter(true);
    }

    static Limiter neverPermit() {
        return new OneAnswerLimiter(false);
    }
}
