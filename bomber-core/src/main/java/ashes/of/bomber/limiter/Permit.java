package ashes.of.bomber.limiter;

public record Permit(long count, long permitted, long timeAwait) {

    public boolean isAllowed() {
        return count == permitted;
    }
}
