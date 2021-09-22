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

}
