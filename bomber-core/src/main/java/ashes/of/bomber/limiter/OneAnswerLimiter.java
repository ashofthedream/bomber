package ashes.of.bomber.limiter;


public class OneAnswerLimiter implements Limiter {
    private final boolean permit;

    public OneAnswerLimiter(boolean permit) {
        this.permit = permit;
    }



    @Override
    public boolean await(int count, long ms) {
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
    public boolean await(int count) {
        return await(count, Long.MAX_VALUE);
    }

    @Override
    public boolean permits(int count) {
        return permit;
    }
}
