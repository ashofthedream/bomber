package ashes.of.loadtest.limiter;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.LockSupport;


public class RateLimiter implements Limiter {

    private static class Permit {
        private final long time;
        private final int position;


        private Permit(long time, int position) {
            this.position = position;
            this.time = time;
        }

        @Override
        public String toString() {
            return "Permit{" +
                    "time=" + time +
                    ", position=" + position +
                    '}';
        }
    }


    private final long duration;
    private final AtomicReferenceArray<Permit> permits;

    private volatile int position;


    public RateLimiter(int count, Duration duration) {
        this.duration = duration.toNanos();
        this.permits = new AtomicReferenceArray<>(count);

        for (int i = 0; i < permits.length(); i++)
            permits.set(i, new Permit(System.nanoTime() - duration.toNanos(), 0));
    }

    @Override
    public boolean waitForAcquire(long ms) {
        long timeUntil = System.currentTimeMillis() + ms;
        while (timeUntil > System.currentTimeMillis()) {
            long timeAwait = acquire();
            if (timeAwait < 0)
                return true;

            LockSupport.parkUntil(Math.min(timeUntil, System.currentTimeMillis() + timeAwait / 1_000_000));
        }

        return false;
    }

    @Override
    public boolean waitForAcquire() {
        while (true) {
            long timeAwait = acquire();
            if (timeAwait < 0)
                return true;

            LockSupport.parkNanos(timeAwait);
        }
    }

    @Override
    public boolean tryAcquire() {
        long timeAwait = acquire();
        return timeAwait < 0;
    }


    private long acquire() {
        long now = System.nanoTime();
        for (int t = 0; t < 100; t++) {
            int current = position;
            int next = Math.max(current + 1, 0);
            int idx = next % permits.length();
            Permit tail = permits.get(idx);


            if (current < tail.position && tail.position - current < permits.length())
                continue;

            long timeAwait = (tail.time + duration) - now;
            if (timeAwait < 0) {
                if (!permits.compareAndSet(idx, tail, new Permit(now, next)))
                    continue;

                position = next;

                return timeAwait;
            }

            return timeAwait;
        }

        return duration;
    }
}
