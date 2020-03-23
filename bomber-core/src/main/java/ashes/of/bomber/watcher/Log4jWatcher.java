package ashes.of.bomber.watcher;

import ashes.of.bomber.core.State;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log4jWatcher implements Watcher {
    private static final Logger log = LogManager.getLogger();

    @Override
    public void watch(State state) {
        long remainInv = state.getRemainInvocations();
        long currentInv = state.getSettings().getTotalInvocationsCount() - remainInv;
        log.info("{} | elapsed: {}ms, remain: {}ms | inv: {}, remain: {}, err: {}",
                state,
                state.getCaseElapsedTime(),
                state.getCaseRemainTime(),
                currentInv,
                remainInv,
                state.getErrorCount());
    }
}
