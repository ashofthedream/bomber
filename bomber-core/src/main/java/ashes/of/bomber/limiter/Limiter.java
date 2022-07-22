package ashes.of.bomber.limiter;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface Limiter {

    /**
     * @see this#await(long)
     */
    default boolean await(int count, Duration duration) {
        return await(count, duration.toMillis());
    }

    default boolean await(Duration duration) {
        return await(1, duration.toMillis());
    }

    /**
     * @see this#await(long)
     */
    default boolean await(int count, long time, TimeUnit unit) {
        return await(count, unit.toMillis(time));
    }

    default boolean await(long time, TimeUnit unit) {
        return await(1, unit.toMillis(time));
    }

    boolean await(int count, long ms);

    default boolean await(long ms) {
        return await(1, ms);
    }


    boolean await(int count);

    default boolean await() {
        return await(1);
    }


    boolean permits(int count);

    default boolean permit() {
        return permits(1);
    }
}
