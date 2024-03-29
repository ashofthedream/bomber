package ashes.of.bomber.benchmark;

import ashes.of.bomber.limiter.Limiter;
import ashes.of.bomber.limiter.OneAnswerLimiter;
import ashes.of.bomber.limiter.RateLimiter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
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
    private final Limiter rateAlwaysPass = new RateLimiter(100_000, Duration.ofMillis(1));
    private final Limiter rateOnlyOnePass = new RateLimiter(1, Duration.ofHours(24));

    @Benchmark
    public boolean oneAnswerAlwaysPass() {
        return oneAnswer.permit();
    }

    @Benchmark
    public boolean rateAlwaysPass() {
        return rateAlwaysPass.permit();
    }

    @Benchmark
    public boolean rateOnlyOnePass() {
        return rateOnlyOnePass.permit();
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
