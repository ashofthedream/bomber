package ashes.of.bomber.carrier.dto.events;

public class HistogramDto {
    private String label;
    private long timestamp;
    private long totalCount;
    private long errorsCount;

    private double[] percentiles;


    public String getLabel() {
        return label;
    }

    public HistogramDto setLabel(String label) {
        this.label = label;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public HistogramDto setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getErrorsCount() {
        return errorsCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public HistogramDto setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public HistogramDto setErrorsCount(long errorsCount) {
        this.errorsCount = errorsCount;
        return this;
    }

    public double[] getPercentiles() {
        return percentiles;
    }

    public HistogramDto setPercentiles(double... percentiles) {
        this.percentiles = percentiles;
        return this;
    }

}
