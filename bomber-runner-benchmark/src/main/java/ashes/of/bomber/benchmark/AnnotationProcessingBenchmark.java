package ashes.of.bomber.benchmark;

import ashes.of.bomber.annotations.LoadTest;
import ashes.of.bomber.annotations.LoadTestCase;
import ashes.of.bomber.annotations.LoadTestSuite;
import ashes.of.bomber.flight.Settings;
import ashes.of.bomber.builder.TestSuiteBuilder;
import ashes.of.bomber.builder.TestAppBuilder;
import ashes.of.bomber.tools.Tools;
import org.HdrHistogram.ConcurrentHistogram;
import org.HdrHistogram.Histogram;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


public class AnnotationProcessingBenchmark {

    @LoadTestSuite
    @LoadTest(time = 20, threadIterations = 1_000_000)
    public static class Test {

        private final Histogram histogram = new ConcurrentHistogram(2);

        @LoadTestCase
        public void test(Tools tools) {
            histogram.recordValue(tools.elapsed());
        }
    }

    private static void run(int count, Supplier<Histogram> test) {
        List<Histogram> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(test.get());
        }

        list.stream()
                .skip(8)
                .forEach(h -> {
                    System.out.printf("%16s %16s %16s %16s %n", "Percentile", "Value From", "Value To", "Count");
                    h.percentiles(1).forEach(v -> {
                            System.out.printf("%16.3f %16.3f %16.3f %16d%n", v.getPercentile(),
                                    v.getDoubleValueIteratedFrom(),
                                    v.getDoubleValueIteratedTo(),
                                    v.getTotalCountToThisValue());
                    });
                    System.out.println();
                    System.out.println();
                });

        System.out.println();
        System.out.printf("%16s %16s %16s %16s %16s %16s %16s %16s%n", "median", "95.00", "99.00", "99.90", "99.99", "max", "rate", "count");
        list.stream()
                .skip(0)
                .forEach(AnnotationProcessingBenchmark::print);
    }

    private static Histogram createWithBuilder() {
        Test test = new Test();

        TestSuiteBuilder<Test> suite = new TestSuiteBuilder<Test>()
                .name("create-with-builder")
                .sharedInstance(test)
                    .warmUp(Settings::disabled)
                    .settings(settings -> settings
//                                .time(20_000)
                            .threadCount(1)
                            .threadIterations(1_000_000))
                .testCase("test", Test::test);

        new TestAppBuilder()
                .name("build-and-run")
//                .app(app -> app.sink(new Log4jSink()))
                .addSuite(suite)
                .build()
                .start();

        return test.histogram;
    }

    private static Histogram createWithAnnotations() {
        Test test = new Test();

        new TestAppBuilder()
                .name("create-with-annotations")
//                .app(app -> app.sink(new Log4jSink()))
                .testSuiteObject(test)
                .build()
                .start();

        return test.histogram;
    }

    private static void print(Histogram h) {
        long p9999 = h.getValueAtPercentile(0.9999);
        System.out.printf("%16.3f %16.3f %16.3f %16.3f %16.3f %16.3f %16.3f %,16d %n",
                us(h.getValueAtPercentile(0.5000)),
                us(h.getValueAtPercentile(0.9500)),
                us(h.getValueAtPercentile(0.9900)),
                us(h.getValueAtPercentile(0.9990)),
                us(p9999),
                us(h.getMaxValue()),
                1_000_000_000.0 / p9999,
                h.getTotalCount());
    }

    private static double ms(long value) {
        return value / 1_000_000.0;
    }

    private static double us(long value) {
        return value / 1_000.0;
    }

    public static void main(String... args) {
        int count = 10;
        System.out.println("run with builder");
        run(count, AnnotationProcessingBenchmark::createWithBuilder);

        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println("run with annotations");
        run(count, AnnotationProcessingBenchmark::createWithAnnotations);
    }
}
