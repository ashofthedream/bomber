package ashes.of.bomber.limiter;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;


public class RateLimiter implements Limiter {

    private record State(double remain, long timestamp) {
    }

    private final long tryouts = 5;
    private final long duration;
    private final long limit;
    private final AtomicReference<State> state;


    public RateLimiter(int limit, Duration duration) {
        this.limit = limit;
        this.duration = duration.toNanos();
        this.state = new AtomicReference<>(new State(limit, System.nanoTime()));
    }

    public static Limiter withRate(int limit, Duration duration) {
        return new RateLimiter(limit, duration);
    }

    public static Limiter withRate(int limit, long millis) {
        return withRate(limit, Duration.ofMillis(millis));
    }

    public static Limiter withRate(int limit, long time, TimeUnit unit) {
        return withRate(limit, unit.toMillis(time));
    }


    @Override
    public boolean waitForPermit(int count, long ms) {
        long timeUntil = System.currentTimeMillis() + ms;
        while (timeUntil > System.currentTimeMillis()) {
            Permit permit = permit(count);
            if (permit.isAllowed())
                return true;

            if (permit.timeAwait() == 0)
                return false;

            LockSupport.parkUntil(Math.min(timeUntil, System.currentTimeMillis() + permit.timeAwait() / 1_000_000));
        }

        return false;
    }

    @Override
    public boolean waitForPermit(int count) {
        while (true) {
            Permit permit = permit(count);
            if (permit.isAllowed())
                return true;

            if (permit.timeAwait() == 0)
                return false;

            LockSupport.parkNanos(permit.timeAwait());
        }
    }

    @Override
    public boolean tryPermit(int count) {
        Permit permit = permit(count);
        return permit.isAllowed();
    }


    private Permit permit(int count) {
        // todo what if count will be greater than limit?
        for (int t = 0; t < tryouts; t++) {
            var now = System.nanoTime();
            var current = this.state.get();

            var opd = duration / (limit * 1.0);
            var elapsed = now - current.timestamp();
            var additional = elapsed / opd;
            var remain = Math.min(limit, current.remain() + additional);

            if (remain >= count) {
                if (this.state.compareAndSet(current, new State(remain - count, now))) {
                    return new Permit(count, count,0);
                }

                continue;
            }


            return new Permit(count, 0, Math.round(opd * (count - remain)));
        }

        return new Permit(count, 0, 0);
    }
}
