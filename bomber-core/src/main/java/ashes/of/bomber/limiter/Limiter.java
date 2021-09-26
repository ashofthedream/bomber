package ashes.of.bomber.limiter;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


public interface Limiter {

    /**
     * @see this#waitForPermit(long)
     */
    default boolean waitForPermit(int count, Duration duration) {
        return waitForPermit(count, duration.toMillis());
    }

    default boolean waitForPermit(Duration duration) {
        return waitForPermit(1, duration.toMillis());
    }

    /**
     * @see this#waitForPermit(long)
     */
    default boolean waitForPermit(int count, long time, TimeUnit unit) {
        return waitForPermit(count, unit.toMillis(time));
    }

    default boolean waitForPermit(long time, TimeUnit unit) {
        return waitForPermit(1, unit.toMillis(time));
    }

    default boolean waitForPermit(long ms) {
        return waitForPermit(1, ms);
    }

    boolean waitForPermit(int count, long ms);

    default boolean waitForPermit() {
        return waitForPermit(1);
    }

    boolean waitForPermit(int count);


    default boolean tryPermit() {
        return tryPermit(1);
    }

    boolean tryPermit(int count);
}
