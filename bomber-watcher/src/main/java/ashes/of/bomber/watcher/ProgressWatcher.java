package ashes.of.bomber.watcher;

import ashes.of.bomber.core.State;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProgressWatcher implements Watcher {
    private static final Logger log = LogManager.getLogger();

    @Override
    public void onStart(State state) {
        log.warn("watcher onStart. stage: {}, testCase: {}",
                state.getStage(), state.getTestCase());
    }

    @Override
    public void watch(State state) {
        log.info("watcher stage: {}, testCase: {}, time elapsed: {}ms, remain time: {}ms, remain iterations: {},  errors: {}",
                state.getStage(), state.getTestCase(),
                state.getElapsedTime(),
                state.getRemainTime(),
                state.getRemainInvocations(),
                state.getErrorCount() );
    }

    @Override
    public void onEnd(State state) {
        log.warn("watcher onEnd. stage: {}, testCase: {}",
                state.getStage(), state.getTestCase());
    }
}
