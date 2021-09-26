package ashes.of.bomber.limiter;

public class Permit {
    private final long count;
    private final long permitted;
    private final long timeAwait;

    public Permit(long count, long permitted, long timeAwait) {
        this.count = count;
        this.permitted = permitted;
        this.timeAwait = timeAwait;
    }

    public boolean isAllowed() {
        return count == permitted;
    }

    public long getCount() {
        return count;
    }

    public long getTimeAwait() {
        return timeAwait;
    }
}
