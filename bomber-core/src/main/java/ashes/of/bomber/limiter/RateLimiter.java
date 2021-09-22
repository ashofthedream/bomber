package ashes.of.bomber.limiter;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.LockSupport;


public class RateLimiter implements Limiter {

    private final long tryouts = 20;
    private final long duration;
    private final AtomicReferenceArray<Permit> permits;

    private volatile int position;


    public RateLimiter(int count, Duration duration) {
        this.duration = duration.toNanos();
        this.permits = new AtomicReferenceArray<>(count);

        for (int i = 0; i < permits.length(); i++) {
            var time = System.nanoTime();
            permits.set(i, new Permit(time - duration.toNanos(), 0));
        }
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
    public boolean waitForPermit(long ms) {
        long timeUntil = System.currentTimeMillis() + ms;
        while (timeUntil > System.currentTimeMillis()) {
            long timeAwait = permit();
            if (timeAwait < 0)
                return true;

            LockSupport.parkUntil(Math.min(timeUntil, System.currentTimeMillis() + timeAwait / 1_000_000));
        }

        return false;
    }

    @Override
    public boolean waitForPermit() {
        while (true) {
            long timeAwait = permit();
            if (timeAwait < 0)
                return true;

            LockSupport.parkNanos(timeAwait);
        }
    }

    @Override
    public boolean tryPermit() {
        long timeAwait = permit();
        return timeAwait < 0;
    }


    private long permit() {
        long now = System.nanoTime();
        for (int t = 0; t < tryouts; t++) {
            int current = position;
            int next = Math.max(current + 1, 0);
            int index = next % permits.length();
            Permit tail = permits.get(index);

            if (current < tail.position && tail.position - current < permits.length())
                continue;

            long timeAwait = (tail.time + duration) - now;
            if (timeAwait < 0) {
                if (!permits.compareAndSet(index, tail, new Permit(now, next)))
                    continue;

                position = next;
                return timeAwait;
            }

            return timeAwait;
        }

        return duration;
    }
}
