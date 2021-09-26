package ashes.of.bomber.limiter;


public class OneAnswerLimiter implements Limiter {
    private final boolean permit;

    public OneAnswerLimiter(boolean permit) {
        this.permit = permit;
    }

    public static Limiter alwaysPermit() {
        return new OneAnswerLimiter(true);
    }

    public static Limiter neverPermit() {
        return new OneAnswerLimiter(false);
    }

    @Override
    public boolean waitForPermit(int count, long ms) {
        if (!permit) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException ignore) {
                Thread.interrupted();
            }
        }

        return permit;
    }

    @Override
    public boolean waitForPermit(int count) {
        return waitForPermit(count, Long.MAX_VALUE);
    }

    @Override
    public boolean tryPermit(int count) {
        return permit;
    }
}
