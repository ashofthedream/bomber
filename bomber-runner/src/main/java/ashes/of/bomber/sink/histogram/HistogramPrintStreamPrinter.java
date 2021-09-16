package ashes.of.bomber.sink.histogram;

import org.HdrHistogram.Histogram;

import java.io.PrintStream;

public class HistogramPrintStreamPrinter implements HistogramPrinter {

    private final PrintStream out;

    public HistogramPrintStreamPrinter(PrintStream out) {
        this.out = out;
    }

    public HistogramPrintStreamPrinter() {
        this(System.out);
    }

    @Override
    public void print(MeasurementKey key, Measurements measurements) {
        out.println("--------------------------------------------------------------------------------");
        out.printf("%s -> %s -> %s%n", key.getTestSuite(), key.getTestSuite(), key.getTestCase());

        measurements.getHistograms().forEach((label, hae) -> {
            out.printf("label: %s, errors: %,12d%n", label, hae.getErrorsCount());
            // clones the histogram but not its content
            Histogram h = new Histogram(hae.getHistogram());
            h.add(hae.getHistogram());
            h.outputPercentileDistribution(out, 1_000_000.0);

            out.println();
            out.println();
        });
    }
}
