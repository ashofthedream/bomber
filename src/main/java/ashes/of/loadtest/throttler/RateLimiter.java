package ashes.of.loadtest.throttler;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.LockSupport;


public class RateLimiter implements Limiter {

    private static class Node {
        private final long time;
        private final int position;


        private Node(long time, int position) {
            this.position = position;
            this.time = time;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "time=" + time +
                    ", position=" + position +
                    '}';
        }
    }


    private final long duration;
    private final AtomicReferenceArray<Node> array;

    private volatile int position;


    public RateLimiter(int count, Duration duration) {
        this.duration = duration.toNanos();
        this.array = new AtomicReferenceArray<>(count);

        for (int i = 0; i < array.length(); i++)
            array.set(i, new Node(System.nanoTime() - duration.toNanos(), 0));
    }

    @Override
    public boolean awaitForPass(long ms) {
        long timeUntil = System.currentTimeMillis() + ms;
        while (timeUntil > System.currentTimeMillis()) {
            long timeAwait = pass();
            if (timeAwait < 0)
                return true;

            LockSupport.parkUntil(Math.min(timeUntil, System.currentTimeMillis() + timeAwait / 1_000_000));
        }

        return false;
    }

    @Override
    public boolean awaitForPass() {
        while (true) {
            long timeAwait = pass();
            if (timeAwait < 0)
                return true;

            LockSupport.parkNanos(timeAwait);
        }
    }

    @Override
    public boolean tryPass() {
        long timeAwait = pass();
        return timeAwait < 0;
    }


    private long pass() {
        long now = System.nanoTime();
        for (int t = 0; t < 100; t++) {
            int current = position;
            int next = Math.max(current + 1, 0);
            int idx = next % array.length();
            Node tail = array.get(idx);


            if (current < tail.position && tail.position - current < array.length())
                continue;

            long timeAwait = (tail.time + duration) - now;
            if (timeAwait < 0) {
                if (!array.compareAndSet(idx, tail, new Node(now, next)))
                    continue;

                position = next;

                return timeAwait;
            }

            return timeAwait;
        }

        return duration;
    }
}
