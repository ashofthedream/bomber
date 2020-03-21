package ashes.of.bomber.watcher;

import ashes.of.bomber.core.State;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProgressWatcher implements Watcher {
    private static final Logger log = LogManager.getLogger();

    @Override
    public void onStart(State state) {
        log.warn("watcher onStart. stage: {}, testCase: {}",
                state.getStage(), state.getTestSuite());
    }

    @Override
    public void watch(State state) {
        log.info("watcher stage: {}, testSuite: {}, elapsed time: {}ms, remain time: {}ms, remain iterations: {},  errors: {}",
                state.getStage(), state.getTestSuite(),
                state.getCaseElapsedTime(),
                state.getCaseRemainTime(),
                state.getRemainInvocations(),
                state.getErrorCount() );
    }

    @Override
    public void onEnd(State state) {
        log.warn("watcher onEnd. stage: {}, testCase: {}",
                state.getStage(), state.getTestSuite());
    }
}
