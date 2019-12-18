package ashes.of.bomber.core.limiter;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class LimiterBenchmark {

    private final Limiter oneAnswer = new OneAnswerLimiter(true);
    private final Limiter rateAlwaysPass = new RateLimiter(100_000, Duration.ofMillis(10));
    private final Limiter rateOnlyOnePass = new RateLimiter(1, Duration.ofHours(24));

    @Benchmark
    public boolean oneAnswerAlwaysPass() {
        return oneAnswer.tryPermit();
    }

    @Benchmark
    public boolean rateAlwaysPass() {
        return rateAlwaysPass.tryPermit();
    }

    @Benchmark
    public boolean rateOnlyOnePass() {
        return rateOnlyOnePass.tryPermit();
    }

    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .forks(2)
                .include(LimiterBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .warmupTime(TimeValue.seconds(3))
                .warmupIterations(5)
                .measurementTime(TimeValue.seconds(5))
                .measurementIterations(5)
                .mode(Mode.Throughput)
                .build();

        Runner runner = new Runner(opt);
        runner.run();
    }
}
