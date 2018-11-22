package ashes.of.loadtest.throttler;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


public interface Limiter {

    /**
     * @see this#awaitForPass(long)
     */
    default boolean awaitForPass(Duration duration) {
        return awaitForPass(duration.toMillis());
    }

    /**
     * @see this#awaitForPass(long)
     */
    default boolean awaitForPass(long time, TimeUnit unit) {
        return awaitForPass(unit.toMillis(time));
    }


    boolean awaitForPass(long ms);

    boolean awaitForPass();

    boolean tryPass();


    static Limiter withRate(int count, Duration duration) {
        return new RateLimiter(count, duration);
    }

    static Limiter withRate(int count, long millis) {
        return withRate(count, Duration.ofMillis(millis));
    }

    static Limiter withRate(int count, long time, TimeUnit unit) {
        return withRate(count, unit.toMillis(time));
    }


    static Limiter alwaysPass() {
        return new OneAnswerLimiter(true);
    }

    static Limiter neverPass() {
        return new OneAnswerLimiter(false);
    }
}
