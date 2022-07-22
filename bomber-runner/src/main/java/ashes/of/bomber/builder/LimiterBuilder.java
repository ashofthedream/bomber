package ashes.of.bomber.builder;

import ashes.of.bomber.configuration.Builder;
import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.limiter.OneAnswerLimiter;
import ashes.of.bomber.limiter.RateLimiter;
import com.google.common.base.Preconditions;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class LimiterBuilder implements Builder<Supplier<Limiter>> {

    private boolean shared;
    private int limit = 1;
    private Duration duration = Duration.ofSeconds(1);

    public static Supplier<Limiter> noLimit() {
        return () -> new OneAnswerLimiter(true);
    }

    public static LimiterBuilder rate(int limit, Duration duration) {
        return new LimiterBuilder()
                .limit(limit)
                .duration(duration);
    }

    public static LimiterBuilder rate(int limit, long millis) {
        return rate(limit, Duration.ofMillis(millis));
    }

    public static LimiterBuilder rate(int limit, long time, TimeUnit unit) {
        return rate(limit, unit.toMillis(time));
    }

    public LimiterBuilder shared(boolean shared) {
        this.shared = shared;
        return this;
    }

    public LimiterBuilder limit(int limit) {
        Preconditions.checkArgument(limit >= 0, "limit is negative");
        this.limit = limit;
        return this;
    }

    public LimiterBuilder duration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public LimiterBuilder millis(long millis) {
        return duration(Duration.ofMillis(millis));
    }

    public LimiterBuilder time(long time, TimeUnit unit) {
        return millis(unit.toMillis(time));
    }


    private Supplier<Limiter> build(int limit, Duration duration) {
        Supplier<Limiter> builder = () -> new RateLimiter(limit, duration);

        if (shared) {
            var delayer = builder.get();
            return () -> delayer;
        }

        return builder;
    }

    @Override
    public Supplier<Limiter> build() {
        return build(limit, duration);
    }
}
