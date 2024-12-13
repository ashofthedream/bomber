package ashes.of.bomber.carrier.dto.events;

public class HistogramPointDto {
    private String label;
    private long timestamp;
    private long totalCount;
    private long errorsCount;

    private double[] percentiles;


    public String getLabel() {
        return label;
    }

    public HistogramPointDto setLabel(String label) {
        this.label = label;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public HistogramPointDto setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getErrorsCount() {
        return errorsCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public HistogramPointDto setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public HistogramPointDto setErrorsCount(long errorsCount) {
        this.errorsCount = errorsCount;
        return this;
    }

    public double[] getPercentiles() {
        return percentiles;
    }

    public HistogramPointDto setPercentiles(double... percentiles) {
        this.percentiles = percentiles;
        return this;
    }
}
