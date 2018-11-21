package ashes.of.loadtest.sink;

import ashes.of.loadtest.runner.TestContext;
import ashes.of.loadtest.settings.Settings;
import ashes.of.loadtest.Stage;
import ashes.of.loadtest.stopwatch.Stopwatch;
import org.HdrHistogram.ConcurrentHistogram;
import org.HdrHistogram.Histogram;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.LongAdder;


public class HdrHistogramSink implements Sink {
    private final NavigableMap<Instant, Histogram> histograms = new ConcurrentSkipListMap<>();
    private final NavigableMap<Instant, LongAdder> errors = new ConcurrentSkipListMap<>();


    @Override
    public void beforeRun(Stage stage, String testCase, Instant startTime, Settings settings) {
        histograms.clear();
        errors.clear();
    }

    @Override
    public void afterTest(TestContext context, long elapsed, Stopwatch stopwatch, @Nullable Throwable throwable) {
        Instant startTime = context.getStartTime().truncatedTo(ChronoUnit.SECONDS);
        histograms.computeIfAbsent(startTime, k -> new ConcurrentHistogram(2))
                .recordValue(elapsed);

        if (throwable == null)
            return;

        errors.computeIfAbsent(startTime, k -> new LongAdder())
                .increment();
    }

    @Override
    public void afterRun(Stage stage, String testCase, Instant startTime, Settings settings) {
        Histogram overall = new ConcurrentHistogram(2);
        histograms.values()
                .forEach(overall::add);

        overall.outputPercentileDistribution(System.out, 1_000_000.0);
    }
}
