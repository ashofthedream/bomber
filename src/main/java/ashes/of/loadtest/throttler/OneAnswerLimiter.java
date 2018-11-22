package ashes.of.loadtest.throttler;


public class OneAnswerLimiter implements Limiter {
    private final boolean answer;

    public OneAnswerLimiter(boolean answer) {
        this.answer = answer;
    }

    @Override
    public boolean awaitForPass(long ms) {
        if (!answer) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException ignore) {
            }
        }

        return answer;
    }

    @Override
    public boolean awaitForPass() {
        return awaitForPass(Long.MAX_VALUE);
    }

    @Override
    public boolean tryPass() {
        return answer;
    }
}
