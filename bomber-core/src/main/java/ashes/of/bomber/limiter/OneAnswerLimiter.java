package ashes.of.bomber.limiter;


public class OneAnswerLimiter implements Limiter {
    private final boolean permit;

    public OneAnswerLimiter(boolean permit) {
        this.permit = permit;
    }

    @Override
    public boolean waitForPermit(long ms) {
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
    public boolean waitForPermit() {
        return waitForPermit(Long.MAX_VALUE);
    }

    @Override
    public boolean tryPermit() {
        return permit;
    }
}
