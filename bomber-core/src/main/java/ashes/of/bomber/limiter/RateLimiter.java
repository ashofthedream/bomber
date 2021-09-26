package ashes.of.bomber.limiter;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;


public class RateLimiter implements Limiter {

    private static class State {
        private final long remain;
        private final long timestamp;

        public State(long remain, long timestamp) {
            this.remain = remain;
            this.timestamp = timestamp;
        }
    }

    private final long tryouts = 5;
    private final long duration;
    private final long count;
    private final AtomicReference<State> state;


    public RateLimiter(int count, Duration duration) {
        this.count = count;
        this.duration = duration.toNanos();
        this.state = new AtomicReference<>(new State(count, System.nanoTime()));
    }

    public static Limiter withRate(int count, Duration duration) {
        return new RateLimiter(count, duration);
    }

    public static Limiter withRate(int count, long millis) {
        return withRate(count, Duration.ofMillis(millis));
    }

    public static Limiter withRate(int count, long time, TimeUnit unit) {
        return withRate(count, unit.toMillis(time));
    }


    @Override
    public boolean waitForPermit(int count, long ms) {
        long timeUntil = System.currentTimeMillis() + ms;
        while (timeUntil > System.currentTimeMillis()) {
            long timeAwait = permit(count);
            if (timeAwait <= 0)
                return true;

            LockSupport.parkUntil(Math.min(timeUntil, System.currentTimeMillis() + timeAwait / 1_000_000));
        }

        return false;
    }

    @Override
    public boolean waitForPermit(int count) {
        while (true) {
            long timeAwait = permit(count);
            if (timeAwait <= 0)
                return true;

            LockSupport.parkNanos(timeAwait);
        }
    }

    @Override
    public boolean tryPermit(int count) {
        long timeAwait = permit(count);
        return timeAwait <= 0;
    }


    private long permit(int count) {
        for (int t = 0; t < tryouts; t++) {
            var now = System.nanoTime();
            var current = this.state.get();

            var opd = duration / this.count;
            var timeLeft = (System.nanoTime() - current.timestamp);
            var additional = timeLeft / opd;

            if (current.remain + additional >= count) {
                var newRemain = Math.min(this.count, current.remain + additional) - count;
                if (this.state.compareAndSet(current, new State(newRemain, now))) {
                    return -1;
                }

                continue;
            }

            return opd * (count - (current.remain + additional));
        }

        return duration;
    }
}
