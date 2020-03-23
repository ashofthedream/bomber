package ashes.of.bomber.limiter;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


public interface Limiter {

    /**
     * @see this#waitForPermit(long)
     */
    default boolean waitForPermit(Duration duration) {
        return waitForPermit(duration.toMillis());
    }

    /**
     * @see this#waitForPermit(long)
     */
    default boolean waitForPermit(long time, TimeUnit unit) {
        return waitForPermit(unit.toMillis(time));
    }

    boolean waitForPermit(long ms);

    boolean waitForPermit();

    boolean tryPermit();


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
